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

public class Day implements Node<Day> {

    private static final int hoursPerDay = 24;

    private final Hour[] hours;
    private final RistrettoPoint aggregatedPoint;

    public Day(final RistrettoPoint zeroPoint) {
        this(newHoursArray(zeroPoint), zeroPoint);
    }

    public Day(final Hour[] hours, final RistrettoPoint aggregatedPoint) {
        this.hours = hours;
        this.aggregatedPoint = aggregatedPoint;
    }

    public Day applyChange(final Change change) {
        final Hour[] newHours = new Hour[hoursPerDay];
        System.arraycopy(this.hours, 0, newHours, 0, hoursPerDay);
        final int hourIndex = change.hourIndex();
        newHours[hourIndex] = new Hour(hours[hourIndex].point().add(change.pointDelta()));

        return new Day(newHours, aggregatedPoint.add(change.pointDelta()));
    }

    @Override
    public RistrettoPoint point() {
        return aggregatedPoint;
    }

    public Hour hour(final int index) {
        return hours[index];
    }

    private static Hour[] newHoursArray(final RistrettoPoint zeroPoint) {
        final Hour[] newHours = new Hour[hoursPerDay];
        final Hour emptyHour = new Hour(zeroPoint);
        Arrays.fill(newHours, emptyHour);
        return newHours;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        final Day day = (Day) o;
        return Objects.deepEquals(hours, day.hours) && Objects.equals(aggregatedPoint, day.aggregatedPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(hours), aggregatedPoint);
    }

}
