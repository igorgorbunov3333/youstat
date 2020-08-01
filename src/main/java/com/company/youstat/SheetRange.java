package com.company.youstat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SheetRange {
    private Map<String, String> map = Stream.of(new String[][]{
            {"A1", "id"},
            {"B1", "name"},
            {"C1", "length"},
            {"D1", "uploadDate"},
            {"E1", "views"},
            {"F1", "likes"},
            {"G1", "dislikes"},
            {"H1", "comments"}
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    private List<List<Object>> rangeValues = new ArrayList<>();

    public SheetRange(String range, Collection<VideoItem> videoItems) {
        String firstRawInRangeValue = map.get(range);
        this.rangeValues.add(Collections.singletonList(firstRawInRangeValue));

        for (VideoItem videoItem : videoItems) {
            switch (firstRawInRangeValue) {
                case "id":
                    this.rangeValues.add(Collections.singletonList(videoItem.getId()));
                    break;
                case "name":
                    this.rangeValues.add(Collections.singletonList(videoItem.getName()));
                    break;
                case "length":
                    this.rangeValues.add(Collections.singletonList(videoItem.getLength()));
                    break;
                case "uploadDate":
                    this.rangeValues.add(Collections.singletonList(videoItem.getUploadDate()));
                    break;
                case "views":
                    this.rangeValues.add(Collections.singletonList(videoItem.getViews()));
                    break;
                case "likes":
                    this.rangeValues.add(Collections.singletonList(videoItem.getLikes()));
                    break;
                case "dislikes":
                    this.rangeValues.add(Collections.singletonList(videoItem.getDislikes()));
                    break;
                case "comments":
                    this.rangeValues.add(Collections.singletonList(videoItem.getComments()));
                    break;
            }

        }
    }

    private SheetRange() {
    }

    public List<List<Object>> getValues() {
        return this.rangeValues;
    }
}
