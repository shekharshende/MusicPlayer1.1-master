package com.example.root.musicplayer.Comparator;

import com.example.root.musicplayer.modelClass.Media;

import java.util.Comparator;

/**
 * Created by root on 27/8/15.
 */
public class FileComparator implements Comparator<Media> {
    @Override
    public int compare(Media lhs, Media rhs) {
        if (lhs == rhs)
            return 0;

        if (lhs.getFile().isDirectory() && rhs.getFile().isFile())
            // Show directories above files
            return -1;

        if (lhs.getFile().isFile() && rhs.getFile().isDirectory())
            // Show files below directories
            return 1;

        // Sort the directories alphabetically
        return lhs.getFile().getName().compareToIgnoreCase(rhs.getFile().getName());
    }
}

