/*
 * Time-Partitioned Monoid Tree for Teragrep (tmt_01)
 * Copyright (C) 2026 Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.tmt_01.node;

import com.teragrep.tmt_01.Change;
import com.teragrep.tmt_01.RistrettoPoint;

import java.util.Arrays;
import java.util.Objects;

public final class Root implements Node<Root> {

    private final RistrettoPoint zeroPoint;

    private final long version;

    private final int[] yearKeys;
    private final Year[] yearValues;
    private final RistrettoPoint aggregatedPoint;

    public Root(final RistrettoPoint zeroPoint) {
        this(new int[0], new Year[0], zeroPoint, 0L, zeroPoint);
    }

    public Root(
            final int[] yearKeys,
            final Year[] yearValues,
            final RistrettoPoint aggregatedPoint,
            final long version,
            final RistrettoPoint zeroPoint
    ) {
        this.yearKeys = yearKeys;
        this.yearValues = yearValues;
        this.aggregatedPoint = aggregatedPoint;
        this.version = version;
        this.zeroPoint = zeroPoint;
    }

    public long version() {
        return version;
    }

    @Override
    public RistrettoPoint point() {
        return aggregatedPoint;
    }

    @Override
    public Root applyChange(final Change change) {

        final int yearIndex = change.yearIndex();

        // 1. Check if we are updating an existing year
        int insertPos = -1;
        for (int i = 0; i < yearKeys.length; i++) {
            if (yearKeys[i] == yearIndex) {
                insertPos = i;
                break;
            }
        }

        final Root rv;
        if (insertPos != -1) {
            // update existing new
            final Year[] newYears = new Year[yearValues.length];
            System.arraycopy(yearValues, 0, newYears, 0, yearValues.length);
            newYears[insertPos] = yearValues[insertPos].applyChange(change);
            rv = new Root(yearKeys, newYears, aggregatedPoint.add(change.pointDelta()), change.version(), zeroPoint);
        }
        else {
            // new year to a new array
            final int[] newYearKeys = new int[yearKeys.length + 1];
            final Year[] newYearValues = new Year[yearValues.length + 1];

            System.arraycopy(yearKeys, 0, newYearKeys, 0, yearKeys.length);
            System.arraycopy(yearValues, 0, newYearValues, 0, yearValues.length);

            newYearKeys[yearKeys.length] = yearIndex;
            newYearValues[yearValues.length] = new Year(zeroPoint).applyChange(change);

            rv = new Root(
                    newYearKeys,
                    newYearValues,
                    aggregatedPoint.add(change.pointDelta()),
                    change.version(),
                    zeroPoint
            );
        }

        return rv;
    }

    public Year year(final int index) {
        for (int i = 0; i < yearKeys.length; i++) {
            if (yearKeys[i] == index) {
                return yearValues[i];
            }
        }
        throw new IllegalArgumentException("Year index " + index + " not found");
    }

    public int[] yearKeys() {
        return yearKeys.clone();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Root root = (Root) o;
        return version == root.version && Objects.equals(zeroPoint, root.zeroPoint) && Objects
                .deepEquals(yearKeys, root.yearKeys) && Objects.deepEquals(yearValues, root.yearValues)
                && Objects.equals(aggregatedPoint, root.aggregatedPoint);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(zeroPoint, version, Arrays.hashCode(yearKeys), Arrays.hashCode(yearValues), aggregatedPoint);
    }

}
