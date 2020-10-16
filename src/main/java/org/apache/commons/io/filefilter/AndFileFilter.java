/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A {@link java.io.FileFilter} providing conditional AND logic across a list of
 * file filters. This filter returns {@code true} if all filters in the
 * list return {@code true}. Otherwise, it returns {@code false}.
 * Checking of the file filter list stops when the first filter returns
 * {@code false}.
 *
 * @since 1.0
 * @see FileFilterUtils#and(IOFileFilter...)
 */
public class AndFileFilter
        extends AbstractFileFilter
        implements ConditionalFileFilter, Serializable {

    private static final long serialVersionUID = 7215974688563965257L;

    /** The list of file filters. */
    private final List<IOFileFilter> fileFilters;

    /**
     * Constructs a new instance of <code>AndFileFilter</code>.
     *
     * @since 1.1
     */
    public AndFileFilter() {
        this.fileFilters = new ArrayList<>();
    }

    /**
     * Constructs a new file filter that ANDs the result of other filters.
     *
     * @param filter1  the first filter, must second be null
     * @param filter2  the first filter, must not be null
     * @throws IllegalArgumentException if either filter is null
     */
    public AndFileFilter(final IOFileFilter filter1, final IOFileFilter filter2) {
        if (filter1 == null || filter2 == null) {
            throw new IllegalArgumentException("The filters must not be null");
        }
        this.fileFilters = new ArrayList<>(2);
        this.fileFilters.add(filter1);
        this.fileFilters.add(filter2);
    }

    /**
     * Constructs a new file filter that ANDs the result of other filters.
     *
     * @param filter1  the first filter, must not be null
     * @param filters  more filters, must not be null
     * @throws IllegalArgumentException if either filter is null
     */
    public AndFileFilter(final IOFileFilter filter1, final IOFileFilter... filters) {
        if (filter1 == null || filters == null) {
            throw new IllegalArgumentException("The filters must not be null");
        }
        this.fileFilters = new ArrayList<>(filters.length + 1);
        this.fileFilters.add(filter1);
        this.fileFilters.addAll(Arrays.asList(filters));
    }

    /**
     * Constructs a new instance of <code>AndFileFilter</code>
     * with the specified list of filters.
     *
     * @param fileFilters  a List of IOFileFilter instances, copied, null ignored
     * @since 1.1
     */
    public AndFileFilter(final List<IOFileFilter> fileFilters) {
        if (fileFilters == null) {
            this.fileFilters = new ArrayList<>();
        } else {
            this.fileFilters = new ArrayList<>(fileFilters);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File file) {
        if (isEmpty()) {
            return false;
        }
        for (final IOFileFilter fileFilter : fileFilters) {
            if (!fileFilter.accept(file)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File file, final String name) {
        if (isEmpty()) {
            return false;
        }
        for (final IOFileFilter fileFilter : fileFilters) {
            if (!fileFilter.accept(file, name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * @since 2.9.0
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) throws IOException {
        if (isEmpty()) {
            return FileVisitResult.TERMINATE;
        }
        for (final IOFileFilter fileFilter : fileFilters) {
            if (fileFilter.accept(file, attributes) != FileVisitResult.CONTINUE) {
                return FileVisitResult.TERMINATE;
            }
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFileFilter(final IOFileFilter ioFileFilter) {
        this.fileFilters.add(ioFileFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IOFileFilter> getFileFilters() {
        return Collections.unmodifiableList(this.fileFilters);
    }

    private boolean isEmpty() {
        return this.fileFilters.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeFileFilter(final IOFileFilter ioFileFilter) {
        return this.fileFilters.remove(ioFileFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileFilters(final List<IOFileFilter> fileFilters) {
        this.fileFilters.clear();
        this.fileFilters.addAll(fileFilters);
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("(");
        for (int i = 0; i < fileFilters.size(); i++) {
            if (i > 0) {
                buffer.append(",");
            }
            final Object filter = fileFilters.get(i);
            buffer.append(filter == null ? "null" : filter.toString());
        }
        buffer.append(")");
        return buffer.toString();
    }

}
