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

package annotations.org.apache.commons.text;

/**
 * Determines if a character array portion matches.
 *
 * @since 1.3
 */
public interface StringMatcher {

    /**
     * Returns the number of matching characters, zero for no match.
     * <p>
     * This method is called to check for a match. The parameter <code>pos</code> represents the current position to be
     * checked in the string <code>buffer</code> (a character array which must not be changed). The API guarantees that
     * <code>pos</code> is a valid index for <code>buffer</code>.
     * <p>
     * The character array may be larger than the active area to be matched. Only values in the buffer between the
     * specified indices may be accessed.
     * <p>
     * The matching code may check one character or many. It may check characters preceding <code>pos</code> as well as
     * those after, so long as no checks exceed the bounds specified.
     * <p>
     * It must return zero for no match, or a positive number if a match was found. The number indicates the number of
     * characters that matched.
     *
     * @param buffer
     *            the text content to match against, do not change
     * @param pos
     *            the starting position for the match, valid for buffer
     * @param bufferStart
     *            the first active index in the buffer, valid for buffer
     * @param bufferEnd
     *            the end index (exclusive) of the active buffer, valid for buffer
     * @return the number of matching characters, or zero if there is no match
     */
    int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd);

}
