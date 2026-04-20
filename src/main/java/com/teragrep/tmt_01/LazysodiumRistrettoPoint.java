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
package com.teragrep.tmt_01;

import com.goterl.lazysodium.LazySodium;
import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.interfaces.Ristretto255;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public final class LazysodiumRistrettoPoint implements RistrettoPoint {

    private final byte[] pointBytes;
    private final LazySodium sodium;

    public LazysodiumRistrettoPoint() {
        this(new LazySodiumJava(new SodiumJava()), new byte[Ristretto255.RISTRETTO255_BYTES]);
    }

    public LazysodiumRistrettoPoint(final LazySodium sodium, final byte[] pointBytes) {
        this.sodium = sodium;
        this.pointBytes = pointBytes;
    }

    @Override
    public RistrettoPoint add(final RistrettoPoint other) {
        if (pointBytes.length != Ristretto255.RISTRETTO255_BYTES) {
            throw new IllegalArgumentException("Ristretto255 points must be exactly 32 bytes.");
        }

        final byte[] result = new byte[Ristretto255.RISTRETTO255_BYTES];
        final boolean success = sodium.cryptoCoreRistretto255Add(result, this.pointBytes, other.toBytes());

        if (!success) {
            throw new IllegalStateException("Libsodium point addition failed.");
        }

        return new LazysodiumRistrettoPoint(sodium, result);
    }

    @Override
    public byte[] toBytes() {
        if (pointBytes.length != Ristretto255.RISTRETTO255_BYTES) {
            throw new IllegalArgumentException("Ristretto255 points must be exactly 32 bytes.");
        }

        return pointBytes.clone();
    }

    @Override
    public String toString() {
        return Base64.getEncoder().encodeToString(pointBytes);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        final LazysodiumRistrettoPoint that = (LazysodiumRistrettoPoint) o;
        // sodium is not part of the object identity
        return Objects.deepEquals(pointBytes, that.pointBytes);
    }

    @Override
    public int hashCode() {
        // sodium is not part of the object identity
        return Arrays.hashCode(pointBytes);
    }

}
