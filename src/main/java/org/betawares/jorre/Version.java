/*
 * ISC License
 *
 * Copyright (c) 2016, Betawares
 *
 * Permission to use, copy, modify, and/or distribute this software for any 
 * purpose with or without fee is hereby granted, provided that the above 
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, 
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM 
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR 
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR 
 * PERFORMANCE OF THIS SOFTWARE.
 */

package org.betawares.jorre;

import java.io.Serializable;

/**
 * A class that represents an immutable application version that can be compared 
 * to other versions.
 * 
 * A version consists of 3 integers representing a major release number, a minor 
 * release number and a patch number plus a string the contains a description of 
 * a minor patch.  Minor patch is not relevant for comparison.
 * 
 */
public class Version implements Serializable, Comparable<Version> {

    private final int major;
    private final int minor;
    private final int patch;
    private final String minorPatch; // not used for comparisons

    private String info = "";

    /**
     * Create a new empty Version
     */
    public Version() {
        this.major = 0;
        this.minor = 0;
        this.patch = 0;
        this.minorPatch = "";
    }
    
    /**
     * Create a new Version
     * 
     * @param major     major release number
     * @param minor     minor release number
     * @param patch     patch number
     * @param minorPatch    minor patch description
     * @param info      additional info
     */
    public Version(int major, int minor, int patch, String minorPatch, String info) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.minorPatch = minorPatch;
        this.info = info;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getMinorPatch() {
        return minorPatch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch + info + minorPatch;
    }

    @Override
    public int compareTo(Version o) {
        if (major != o.major) {
            return major - o.major;
        }
        if (minor != o.minor) {
            return minor - o.minor;
        }
        if (patch != o.patch) {
            return patch - o.patch;
        }
        return info.compareTo(o.info);
    }

}
