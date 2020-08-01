package com.company.youstat;

/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Print a list of videos uploaded to the authenticated user's YouTube channel.
 *
 * @author Jeremy Walker
 */
public class UploadVideosAnalytic {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Authorize the user, call the youtube.channels.list method to retrieve
     * the playlist ID for the list of videos uploaded to the user's channel,
     * and then call the youtube.playlistItems.list method to retrieve the
     * list of videos in that playlist.
     *
     * @param args command line args (not used).
     */
    public static void main(String[] args) {

        // This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = Lists.newArrayList(
                "https://www.googleapis.com/auth/youtube.readonly",
                "https://www.googleapis.com/auth/spreadsheets");

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "myuploads");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(
                    "youtube-cmdline-myuploads-sample").build();

            // Call the API's channels.list method to retrieve the
            // resource that represents the authenticated user's channel.
            // In the API response, only include channel information needed for
            // this use case. The channel's contentDetails part contains
            // playlist IDs relevant to the channel, including the ID for the
            // list that contains videos uploaded to the channel.
            YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
            channelRequest.setMine(true);
            channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
            ChannelListResponse channelResult = channelRequest.execute();

            List<Channel> channelsList = channelResult.getItems();

            if (channelsList != null) {
                // The user's default channel is the first item in the list.
                // Extract the playlist ID for the channel's videos from the
                // API response.
                String uploadPlaylistId =
                        channelsList.get(0).getContentDetails().getRelatedPlaylists().getUploads();

                // Define a list to store items in the list of uploaded videos.
                List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

                // Retrieve the playlist of the channel's uploaded videos.
                YouTube.PlaylistItems.List playlistItemRequest =
                        youtube.playlistItems().list("id,contentDetails,snippet");
                playlistItemRequest.setPlaylistId(uploadPlaylistId);

                // Only retrieve data used in this application, thereby making
                // the application more efficient. See:
                // https://developers.google.com/youtube/v3/getting-started#partial
//                playlistItemRequest.setFields(
//                        "items(contentDetails/videoId,snippet/title,snippet/publishedAt,statistics/likeCount,"
//                                + "statistics/dislikeCount,statistics/commentCount,statistics/viewCount),"
//                                + "nextPageToken,pageInfo");

                String nextToken = "";

                // Call the API one or more times to retrieve all items in the
                // list. As long as the API response returns a nextPageToken,
                // there are still more items to retrieve.
                do {
                    playlistItemRequest.setPageToken(nextToken);
                    PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

                    playlistItemList.addAll(playlistItemResult.getItems());

                    nextToken = playlistItemResult.getNextPageToken();
                } while (nextToken != null);

                Map<PlaylistItem, Video> playlistItemVideoMap = new HashMap<>();

                for (PlaylistItem playlistItem : playlistItemList) {
                    YouTube.Videos.List list = youtube.videos().list("statistics,contentDetails");
                    list.setId(playlistItem.getContentDetails().getVideoId());
                    list.setKey("key");
                    Video v = list.execute().getItems().get(0);
                    playlistItemVideoMap.put(playlistItem, v);
                }

                // Prints information about the results.
                writeToGoogleSheets(playlistItemVideoMap);
            }

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * Print information about all of the items in the playlist.
     *
     * @param size size of list
     *
     * @param iterator of Playlist Items from uploaded Playlist
     */
    private static void writeToGoogleSheets(Map<PlaylistItem, Video> playlistEntries) {
        List<VideoItem> videoItems = new ArrayList<>();
        for (Map.Entry<PlaylistItem, Video> entry : playlistEntries.entrySet()) {
            VideoItem videoItem = new VideoItem();
            videoItem.setId(entry.getKey().getContentDetails().getVideoId());
            videoItem.setName(entry.getKey().getSnippet().getTitle());
            videoItem.setLength(entry.getValue().getContentDetails().getDuration());
            videoItem.setUploadDate(entry.getKey().getSnippet().getPublishedAt().toString());
            videoItem.setViews(entry.getValue().getStatistics().getViewCount().toString());
            videoItem.setLikes(entry.getValue().getStatistics().getLikeCount().toString());
            videoItem.setDislikes(entry.getValue().getStatistics().getDislikeCount().toString());
            videoItem.setComments(entry.getValue().getStatistics().getCommentCount().toString());
            videoItems.add(videoItem);
        }

        Credential credential = null;
        try {
            List<String> scopes = Lists.newArrayList(
                    "https://www.googleapis.com/auth/spreadsheets"
            );
            credential = Auth.authorize(scopes, "google_sheets");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            new GoogleSheetApiService(buildService(credential)).create("VideoStatistics", videoItems);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    private static Sheets buildService(Credential credential) throws GeneralSecurityException, IOException {
        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Sheets API Snippets")
                .build();
    }
}


