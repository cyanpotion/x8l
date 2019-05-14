package com.xenoamess.x8l;

import org.junit.jupiter.api.Test;

/**
 * @author XenoAmess
 */
public class VersionTest {
    public static final int compareVersionsTest(String va, String vb) {
        System.out.println("compareVersionsTest : " + va + " " + vb);
        int res = Version.compareVersions(va, vb);
        System.out.println("res : " + res);
        return res;
    }

    @Test
    public final void Test() {
        assert (compareVersionsTest("0.1.0-SNAPSHOT", "0.1.0") < 0);
        assert (compareVersionsTest("0.1.0-SNAPSHOT", "0.25.0") < 0);
        assert (compareVersionsTest("1.1.0-SNAPSHOT", "0.25.0") > 0);
        assert (compareVersionsTest("1.33", "5") < 0);
        assert (compareVersionsTest("1.33.0", "0.44.0") > 0);
        assert (compareVersionsTest("0.1.0-SNAPSHOT", "0.1.0-SNAPSHOT") == 0);
    }
}
