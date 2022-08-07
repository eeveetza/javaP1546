package test;

import main.P1546;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class P1546Test {
    // the results are compared to the MATLAB implementation of Recommendation ITU-R P.1546-6
    // the test is passed when the results for transmission loss are within 0.001 dB or less of difference
    // different antenna heights, time percentages, frequencies, distances, and clutter categories are tested
    //     Rev   Date        Author                          Description
    //     -------------------------------------------------------------------------------
    //     v0    21DEC16     Ivica Stevanovic, OFCOM         Initial version in Java
    //     v1    01NOV19     Ivica Stevanovic, OFCOM         Aligned to ITU-R P.1546-6
    //     v2    21FEB20     Ivica Stevanovic, OFCOM         Added tests for reproducing results in  Figures 1 and 10


    TestUtil util;

    @Before
    public void setup() {

        util = new TestUtil(0.001);
    }


    @Test
    public void test0() {

        P1546 calculator = new P1546();

        // f = 100, t = 1, d = 10, h1 = 20

        double f = 100;
        double t = 1;
        double d = 10;
        double h1 = 20;
        double Emax = 100;

        double E = calculator.step6_10(t, f, h1, 1, d, Emax);


        double expectedResult = 58.58;
        double result = E;
        //System.out.printf("%f\t %f\n", result, expectedResult);
        Assert.assertEquals(expectedResult, result, 1e-3);

        f = 600;
        t = 1;
        d = 10;
        h1 = 20;
        Emax = 100;

        E = calculator.step6_10(t, f, h1, 1, d, Emax);


        expectedResult = 59.27;
        result = E;
        //System.out.printf("%f\t %f\n", result, expectedResult);
        Assert.assertEquals(expectedResult, result, 1e-3);

        f = 2000;
        t = 1;
        d = 10;
        h1 = 20;
        Emax = 100;

        E = calculator.step6_10(t, f, h1, 1, d, Emax);


        expectedResult = 57.28;
        result = E;
        //System.out.printf("%f\t %f\n", result, expectedResult);
        Assert.assertEquals(expectedResult, result, 1e-3);

        // cold sea

        f = 100;
        t = 1;
        d = 600;
        h1 = 150;
        Emax = 100;

        E = calculator.step6_10(t, f, h1, 3, d, Emax);


        expectedResult = 1.441;
        result = E;
        //System.out.printf("%f\t %f\n", result, expectedResult);
        Assert.assertEquals(expectedResult, result, 1e-3);

        f = 100;
        t = 1;
        d = 10;
        h1 = 20;
        Emax = 100;

        E = calculator.step6_10(t, f, h1, 3, d, Emax);


        expectedResult = 67.881;
        result = E;
        //System.out.printf("%f\t %f\n", result, expectedResult);
        Assert.assertEquals(expectedResult, result, 1e-3);

        f = 600;
        t = 1;
        d = 10;
        h1 = 20;
        Emax = 100;

        E = calculator.step6_10(t, f, h1, 3, d, Emax);


        expectedResult = 80.752;
        result = E;
        //System.out.printf("%f\t %f\n", result, expectedResult);
        Assert.assertEquals(expectedResult, result, 1e-3);


        f = 2000;
        t = 1;
        d = 10;
        h1 = 20;
        Emax = 100;

        E = calculator.step6_10(t, f, h1, 3, d, Emax);


        expectedResult = 89.622;
        result = E;
        //System.out.printf("%f\t %f\n", result, expectedResult);
        Assert.assertEquals(expectedResult, result, 1e-3);


        f = 100;
        t = 1;
        d = 1000;
        h1 = 20;
        Emax = 100;

        E = calculator.step6_10(t, f, h1, 3, d, Emax);


        expectedResult = -18.481;
        result = E;
        //System.out.printf("%f\t %f\n", result, expectedResult);
        Assert.assertEquals(expectedResult, result, 1e-3);


        f = 100;
        t = 1;
        d = 1000;
        h1 = 20;
        Emax = 100;

        E = calculator.step6_10(t, f, h1, 2, d, Emax); // warm


        expectedResult = 1.187;
        result = E;
        //System.out.printf("%f\t %f\n", result, expectedResult);
        Assert.assertEquals(expectedResult, result, 1e-3);
    }

    @Test
    public void test1() {

        P1546 calculator = new P1546();

        // b2iseac_land_1km1;
        double PTx = 1;
        double f = 300;
        double t = 10;
        double ha = 50;
        double h2 = 10;
        double[] d_v = {1};
        double d = d_v[0];
        String[] path_c = {"Land"};
        int path = 1;
        double heff = 121.4375;
        double tca = 10.3519;
        double R1 = 0;
        double R2 = 0;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;

        int pathinfo = 1;
        double q = 50;
        double htter = 754.4000;
        double hrter = 610.3000;
        double eff1 = -10.5505;
        double eff2 = 10.3519;
        double hb = 121.4375;

        double[] rDist = new double[1];
        // Results obtained from the MATLAB implementation of ITU-R P.1546-5
        // available on the ITU-R SG3 sharefolder

        rDist[0] = 77.6459;
        /*rDist[1] = 0.2782559402;
        rDist[2] = 0.7742636827;
        rDist[3] = 2.15443469;
        rDist[4] = 5.994842503;
        rDist[5] = 16.68100537;
        rDist[6] = 46.41588834;
        rDist[7] = 129.1549665;
        rDist[8] = 359.3813664;
        rDist[9] = 1000;
*/
        int NN = d_v.length;

        int N = 12;
        double[] expectedResult;
        double[] result;
        expectedResult = new double[N];
        result = new double[N];

        // EmaxF
        double EmaxF = calculator.Step_19a(t, 1, 0);
        EmaxF = EmaxF + calculator.Step_16a(ha, h2, d, htter, hrter);
        expectedResult[0] = 106.755;
        result[0] = EmaxF;

        // h1

        expectedResult[1] = 121.438;
        double h1 = calculator.h1Calc(d, heff, ha, hb, path, pathinfo);
        result[1] = h1;
        // E
        double[] El = new double[1];
        double[] dl = new double[1];
        double[] Es = new double[0];
        double[] ds = new double[0];

        El[0] = calculator.step6_10(t, f, h1, path, d, EmaxF);
        dl[0] = d;
        double E = calculator.Step_11a_rrc06(El, Es, dl, ds);
        expectedResult[2] = 100.721;
        result[2] = E;

        // tca

        double[] tca_corr;
        tca_corr = calculator.Step_12a(f, tca);
        double Correction = tca_corr[0];
        double nu = tca_corr[1];
        E = E + Correction;
        expectedResult[3] = 11.6545;
        result[3] = nu;
        expectedResult[4] = -22.9301;
        result[4] = Correction;

        // theta_s and Ets
        double[] tscat_corr;
        tscat_corr = calculator.Step_13a(d, f, t, eff1, eff2);
        double Ets = tscat_corr[0];
        double theta_s = tscat_corr[1];
        E = Math.max(E, Ets);
        expectedResult[5] = 0.0;
        result[5] = theta_s;
        expectedResult[6] = 70.3176;
        result[6] = Ets;

        if (path_c[NN - 1].equalsIgnoreCase("Land")) {
            path = 1;
        } else if (path_c[NN - 1].equalsIgnoreCase("Warm")) {
            path = 2;
        } else { // "Cold"
            path = 3;
        }

        double[] st14corr;
        st14corr = calculator.Step_14a(h1, d, R2, h2, f, area);
        Correction = st14corr[0];
        double R2p = st14corr[1];
        E = E + Correction;
        expectedResult[7] = 10;
        expectedResult[8] = 0;

        result[7] = R2p;
        result[8] = Correction;
        Correction = 0;
        if (ha >= 0 && R1 >= 0) {
            Correction = calculator.Step_15a(ha, R1, f);
            E = E + Correction;
            //if (debug == 1)
            //fprintf(fid_log,['Tx clutter correction (dB);§10 (30);15;' floatformat], Step_15a(ha,R1,f));
            //end
        }

        result[9] = Correction;
        expectedResult[9] = 0;

        Correction = 0;
        // Step 16: Apply the slope-path correction given in annex 5, Sec. 14
        if (ha >= 0 && h2 < 10000) {
            Correction = calculator.Step_16a(ha, h2, d, htter, hrter);
            E = E + Correction;
            //if (debug == 1)
            //fprintf(fid_log,['Rx slope-path correction (dB);§14 (37);16;' floatformat],Step_16a(ha,h2,d, htter,hrter));
            //end
        }


        result[10] = Correction;
        expectedResult[10] = -0.144755;
        double sigmaL = 0;

        double L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult[11] = 111.197;
        result[11] = L;


        /*expectedResult[1] =    80.4752;
        expectedResult[2] =    89.3666;
        expectedResult[3] =    98.2625;
        expectedResult[4] =   111.5308;
        expectedResult[5] =   132.2907;
        expectedResult[6] =   165.7675;
        expectedResult[7] =   189.6386;
        expectedResult[8] =   215.1282;
        expectedResult[9] =   270.2094;
*/


        for (int ii = 0; ii < NN; ii++) {

            //util.assertDoubleEquals(expectedResult[ii], result[ii]);
            Assert.assertEquals(expectedResult[ii], result[ii], 1e-3);
        }
    }

    @Test
    public void test2() {

        P1546 calculator = new P1546();

        // b2iseac_land_10km1;
        double tca = 4.9153;
        double PTx = 1;
        double f = 900;
        double t = 20;
        double q = 50;
        double heff = 478.1125;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {10};
        String[] path_c = {"Land"};
        double h2 = 5;
        double ha = 100;
        double htter = 754.4000;
        double hrter = 250.3000;
        double eff1 = -2.6258;
        double eff2 = 4.9153;
        double hb = 478.1125;
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double sigmaL = 0;

        int NN = d_v.length;

        double L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        double expectedResult = 140.9834;
        double result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

    }

    @Test
    public void test3() {

        P1546 calculator = new P1546();

        // b2iseac_land_100km1;
        double tca = -0.003581;
        double PTx = 1;
        double f = 2600;
        double t = 50;
        double q = 50;
        double heff = 1479.4333;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {100};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 1;
        double ha = 1000;
        double htter = 754.4000;
        double hrter = 0;
        double eff1 = -6.5334;
        double eff2 = -0.003581;
        double hb = -10000;
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double sigmaL = 0;

        int NN = d_v.length;

        double L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        double expectedResult = 186.1854;
        double result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

    }

    @Test
    public void test4() {

        P1546 calculator = new P1546();

        // b2iseac_land;
        double tca = -0.42362;
        double PTx = 1;
        double f = 95.3;
        double t = 1;
        double q = 50;
        double heff = 539.4333;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {235.1};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 7;
        double ha = 60;
        double htter = 754.4000;
        double hrter = 111.3000;
        double eff1 = -2.2739;
        double eff2 = -0.42362;
        double hb = -10000; // not defined
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;
        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 146.4498;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);

        t = 10;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 153.2265;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);

        t = 50;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 161.0868;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);


    }


    @Test
    public void test5() {

        P1546 calculator = new P1546();

        // b2iseac_sea;
        double tca = -0.42362;
        double PTx = 1;
        double f = 95.3;
        double t = 1;
        double q = 50;
        double heff = 539.4333;
        //P1546ver5Input.Environment area=        Sea;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {235.1};
        String[] path_c = {"Sea"};
        int Nimporte = 2;
        double h2 = 7;
        double ha = 60;
        double htter = 754.4000;
        double hrter = 111.3000;
        double eff1 = -2.2739;
        double eff2 = -0.42362;
        double hb = -10000; // not defined
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 146.4498;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);

        t = 10;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 153.2265;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);

        t = 50;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 161.0868;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);


    }


    @Test
    public void test6() {

        P1546 calculator = new P1546();
        // b2iseac_land_sea_coast;
        double tca = -0.42362;
        double PTx = 1;
        double f = 95.3;
        double t = 1;
        double q = 50;
        double heff = 539.4333;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {21.75, 213.35};
        String[] path_c = {"Land", "Sea"};
        int Nimporte = 2;
        double h2 = 7;
        double ha = 60;
        double htter = 754.4000;
        double hrter = 111.3000;
        double eff1 = -2.2739;
        double eff2 = -0.42362;
        double hb = -10000; // not defined
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 146.4498;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);

        t = 10;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 153.2265;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);

        t = 50;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 161.0868;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);


    }

    @Test
    public void test7() {

        P1546 calculator = new P1546();

        // flat_1km
        double tca = -0.57294;
        double PTx = 1;
        double f = 300;
        double t = 10;
        double q = 50;
        double heff = 50;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {1};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 10;
        double ha = 50;
        double htter = 0;
        double hrter = 0;
        double eff1 = -2.8624;
        double eff2 = -0.57294;
        double hb = 50;
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 91.5752;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }

    @Test
    public void test8() {

        P1546 calculator = new P1546();

        // flat_10km
        double tca = -0.028648;
        double PTx = 1;
        double f = 900;
        double t = 20;
        double q = 50;
        double heff = 100;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {10};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 5;
        double ha = 100;
        double htter = 0;
        double hrter = 0;
        double eff1 = -0.57294;
        double eff2 = -0.028648;
        double hb = 100;
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 135.3539;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }

    @Test
    public void test9() {

        P1546 calculator = new P1546();

        // flat_100km
        double tca = -0.003581;
        double PTx = 1;
        double f = 2600;
        double t = 50;
        double q = 50;
        double heff = 1000;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {100};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 1;
        double ha = 1000;
        double htter = 0;
        double hrter = 0;
        double eff1 = -4.0856;
        double eff2 = -0.003581;
        double hb = -10000; // not defined
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 194.9979;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }

    @Test
    public void test10() {

        P1546 calculator = new P1546();

        // flat_0.1km
        double tca = -5.71059;
        double PTx = 1;
        double f = 90;
        double t = 1;
        double q = 50;
        double heff = 100;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {0.1};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 10;
        double ha = 100;
        double htter = 0;
        double hrter = 0;
        double eff1 = -45;
        double eff2 = -5.71059;
        double hb = 100; // not defined
        double R1 = 10;
        double R2 = 10;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 55.1909;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-4);


    }


    @Test
    public void test11() {

        P1546 calculator = new P1546();
        // misc;
        double tca = 1.8233;
        double PTx = 1;
        double f = 95.3;
        double t = 1;
        double q = 50;
        double heff = 61;
        //P1546ver5Input.Environment area=        Sea;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {0.3, 33.4};
        String[] path_c = {"Land", "Sea"};
        int Nimporte = 2;
        double h2 = 7;
        double ha = 60;
        double htter = 1;
        double hrter = 38.7;
        double eff1 = 1.0885;
        double eff2 = 1.8233;
        double hb = -10000; // not defined
        double R1 = 70;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 149.8209;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 10;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 152.3519;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 50;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 153.0929;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }

    @Test
    public void test12() {

        P1546 calculator = new P1546();
        // rburg;
        double tca = -0.19582;
        double PTx = 0.15849;
        double f = 98.20;
        double t = 1;
        double q = 50;
        double heff = 15.1708;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {96.2};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 19;
        double ha = 12;
        double htter = 395;
        double hrter = 496;
        double eff1 = 2.6337;
        double eff2 = -0.19582;
        double hb = -10000; // not defined
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 145.9451;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 10;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 152.1467;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 50;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 162.3618;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }

    @Test
    public void test13() {

        P1546 calculator = new P1546();
        // rburg_loss;
        double tca = -0.84505;
        double PTx = 0.15849;
        double f = 98.20;
        double t = 1;
        double q = 50;
        double heff = 1003.1708;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {96.2};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 200;
        double ha = 1000;
        double htter = 395;
        double hrter = 496;
        double eff1 = -3.7442;
        double eff2 = -0.84505;
        double hb = -10000; // not defined
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 111.9060;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 10;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 111.9060;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 50;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 111.9060;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }

    @Test
    public void test14() {

        P1546 calculator = new P1546();
        // rburg_los_subpath_diffraction;
        double tca = -0.84505;
        double PTx = 0.15849;
        double f = 98.20;
        double t = 1;
        double q = 50;
        double heff = 203.1708;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {96.2};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 200;
        double ha = 200;
        double htter = 395;
        double hrter = 496;
        double eff1 = -0.6314;
        double eff2 = -0.84505;
        double hb = -10000; // not defined
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 116.4705;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 10;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 123.2471;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 50;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 131.1074;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }

    @Test
    public void test15() {

        P1546 calculator = new P1546();
        // rburg_with_clutter;
        double tca = -0.19582;
        double PTx = 0.15849;
        double f = 98.20;
        double t = 1;
        double q = 50;
        double heff = 15.1708;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double[] d_v = {96.2};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 19;
        double ha = 12;
        double htter = 395;
        double hrter = 496;
        double eff1 = 2.6337;
        double eff2 = -0.19582;
        double hb = -10000; // not defined
        double R1 = 10;
        double R2 = 25;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 149.3646;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 10;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 155.5661;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        t = 50;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 165.7812;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }

    @Test
    public void test16() {

        P1546 calculator = new P1546();
        // rburg_with_clutter and location probability q = 20%
        double tca = -0.19582;
        double PTx = 0.15849;
        double f = 98.20;
        double t = 1;
        double q = 20;
        double heff = 15.1708;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.URBAN;
        double[] d_v = {96.2};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 19;
        double ha = 12;
        double htter = 395;
        double hrter = 496;
        double eff1 = 2.6337;
        double eff2 = -0.19582;
        double hb = -10000; // not defined
        double R1 = 10;
        double R2 = 25;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 3.7897449;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 157.9341;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        q = 1;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 152.3051;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);

        q = 75;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 163.678;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


        q = 99;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 169.9409;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }


    @Test
    public void test17() {

        P1546 calculator = new P1546();
        // rburg_with_clutter and location probability q = 20%
        double tca = 10.5697;
        double PTx = 10;
        double f = 562;
        double t = 50;
        double q = 50;
        double heff = 186.462;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.SUBURBAN;
        double[] d_v = {0.637};
        String[] path_c = {"Land"};
        int Nimporte = 2;
        double h2 = 3.34;
        double ha = 95.5;
        double htter = 543.7;
        double hrter = 428.1;
        double eff1 = -18.3351;
        double eff2 = 10.5697;
        double hb = 186.462; // not defined
        double R1 = 0;
        double R2 = 0;
        int pathinfo = 1;
        double L;
        double result;
        double expectedResult;
        int NN = d_v.length;
        double sigmaL = 0;

        L = calculator.P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo,
                q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);


        expectedResult = 111.542;
        result = L;

        Assert.assertEquals(expectedResult, result, 1e-3);


    }


    @Test
    public void test18() {
        // Figure 10
        // In this test the Figures 1-24 from ITU-R P1546-4 are reproduced and
        // compared with the original data extracted from excel tables.

        P1546 calculator = new P1546();
        //// Data extracted from the Excel tables


        double[][][] excelTables = new double[][][]{

                {
                        {78, 10, 20, 37.5, 75, 150, 300, 600, 1200, 0},
                        {1, 92.788, 94.8916, 97.0758, 99.6994, 102.3451, 104.5908, 106.0069, 106.6288, 106.9},
                        {2, 81.9555, 84.7466, 87.4489, 90.6724, 94.0761, 97.2674, 99.5107, 100.5106, 100.8794},
                        {3, 74.8484, 78.4461, 81.6171, 85.2459, 89.0757, 92.8119, 95.6232, 96.9168, 97.3576},
                        {4, 69.3403, 73.6501, 77.2921, 81.2936, 85.4508, 89.5735, 92.8188, 94.3587, 94.8588},
                        {5, 64.8599, 69.686, 73.762, 78.1282, 82.5775, 87.0114, 90.6125, 92.3688, 92.9206},
                        {6, 61.1114, 66.2845, 70.7272, 75.4433, 80.1706, 84.8768, 88.7855, 90.7383, 91.337},
                        {7, 57.9049, 63.3063, 68.0408, 73.0796, 78.0762, 83.0334, 87.2193, 89.3556, 89.998},
                        {8, 55.112, 60.6634, 65.6222, 70.9468, 76.2023, 81.3982, 85.8421, 88.1539, 88.8382},
                        {9, 52.6443, 58.2939, 63.4211, 68.9907, 74.491, 79.9171, 84.6068, 87.0899, 87.8152},
                        {10, 50.4383, 56.1513, 61.4032, 67.1773, 72.9042, 78.5529, 83.4807, 86.1339, 86.9},
                        {11, 48.4478, 54.1999, 59.5426, 65.484, 71.4164, 77.2791, 82.4402, 85.2647, 86.0721},
                        {12, 46.638, 52.4114, 57.8189, 63.8947, 70.0099, 76.0769, 81.4675, 84.4663, 85.3164},
                        {13, 44.982, 50.7636, 56.2155, 62.3973, 68.6725, 74.9323, 80.5491, 83.7268, 84.6211},
                        {14, 43.4587, 49.238, 54.7185, 60.9824, 67.3954, 73.8351, 79.6745, 83.0366, 83.9774},
                        {15, 42.0512, 47.82, 53.3162, 59.6419, 66.1719, 72.7778, 78.8355, 82.3882, 83.3782},
                        {16, 40.7459, 46.497, 51.9984, 58.369, 64.9969, 71.7547, 78.0255, 81.7754, 82.8176},
                        {17, 39.5314, 45.2587, 50.7566, 57.1578, 63.8663, 70.7618, 77.2392, 81.1932, 82.291},
                        {18, 38.3982, 44.0962, 49.5832, 56.0028, 62.7766, 69.7956, 76.4727, 80.6373, 81.7946},
                        {19, 37.3381, 43.0021, 48.4718, 54.8993, 61.7248, 68.8539, 75.7227, 80.1041, 81.3249},
                        {20, 36.3444, 41.9699, 47.4167, 53.8431, 60.708, 67.9344, 74.9865, 79.5907, 80.8794},
                        {25, 32.1859, 37.5625, 42.8285, 49.1477, 56.0708, 63.6221, 71.4552, 77.2388, 78.9412},
                        {30, 29.036, 34.0906, 39.0958, 45.1918, 52.0149, 59.6903, 68.2366, 75.108, 77.3576},
                        {35, 26.5844, 31.2686, 35.9617, 41.7622, 48.3861, 56.0505, 65.1246, 73.1996, 76.0186},
                        {40, 24.6324, 28.9215, 33.2742, 38.7355, 45.0951, 52.6511, 61.9988, 71.2964, 74.8588},
                        {45, 23.0447, 26.935, 30.9385, 36.0396, 42.094, 49.4684, 58.8621, 69.318, 73.8358},
                        {50, 21.7254, 25.2295, 28.8905, 33.6288, 39.3562, 46.4948, 55.7392, 67.2133, 72.9206},
                        {55, 20.6047, 23.7468, 27.0831, 31.4694, 36.8637, 43.7288, 52.7823, 64.9658, 72.0927},
                        {60, 19.631, 22.4419, 25.478, 29.5325, 34.5995, 41.1685, 50.0587, 62.5906, 71.337},
                        {65, 18.7658, 21.2787, 24.0426, 27.7909, 32.5451, 38.8083, 47.4765, 60.1224, 70.6417},
                        {70, 17.98, 20.2284, 22.7486, 26.2189, 30.6803, 36.6383, 45.0419, 57.6014, 69.998},
                        {75, 17.2517, 19.2673, 21.5716, 24.7922, 28.9837, 34.6453, 42.7566, 55.0647, 69.3988},
                        {80, 16.5643, 18.3761, 20.4903, 23.4885, 27.4342, 32.8136, 40.6176, 52.6115, 68.8382},
                        {85, 15.9053, 17.5394, 19.4868, 22.2881, 26.0118, 31.1265, 38.6187, 50.3935, 68.3116},
                        {90, 15.2655, 16.7448, 18.5462, 21.1736, 24.6979, 29.5674, 36.7513, 48.2532, 67.8152},
                        {95, 14.6378, 15.9823, 17.6561, 20.1302, 23.4763, 28.1204, 35.0053, 46.1964, 67.3455},
                        {100, 14.0173, 15.2443, 16.8062, 19.1453, 22.3325, 26.7708, 33.3701, 44.2258, 66.9},
                        {110, 12.7839, 13.8184, 15.1962, 17.3114, 20.2311, 24.3131, 30.3897, 40.5421, 66.0721},
                        {120, 11.5472, 12.4335, 13.6687, 15.6091, 18.3166, 22.1072, 27.7307, 37.1851, 65.3164},
                        {130, 10.2996, 11.0704, 12.1938, 13.9967, 16.5353, 20.0884, 25.326, 34.1233, 64.6211},
                        {140, 9.0392, 9.7188, 10.7538, 12.4476, 14.8508, 18.2102, 23.122, 31.3204, 63.9774},
                        {150, 7.7676, 8.3744, 9.3385, 10.9448, 13.2388, 16.4396, 21.0773, 28.7407, 63.3782},
                        {160, 6.4878, 7.0358, 7.9426, 9.478, 11.6831, 14.7534, 19.1608, 26.3522, 62.8176},
                        {170, 5.2036, 5.7037, 6.5637, 8.0412, 10.1734, 13.1357, 17.349, 24.1271, 62.291},
                        {180, 3.9191, 4.3797, 5.2012, 6.631, 8.7029, 11.5752, 15.6245, 22.042, 61.7946},
                        {190, 2.6379, 3.0658, 3.8553, 5.2455, 7.2672, 10.064, 13.9742, 20.0776, 61.3249},
                        {200, 1.3635, 1.7642, 2.5269, 3.8839, 5.8634, 8.5966, 12.3883, 18.2181, 60.8794},
                        {225, -1.7737, -1.4241, -0.7115, 0.5833, 2.4833, 5.0957, 8.6598, 13.9485, 59.8564},
                        {250, -4.8157, -4.5005, -3.8218, -2.5693, -0.7235, 1.806, 5.2114, 10.1107, 58.9412},
                        {275, -7.7434, -7.4523, -6.7974, -5.5746, -3.7669, -1.2964, 1.9954, 6.6096, 58.1133},
                        {300, -10.55, -10.2762, -9.6385, -8.4372, -6.6573, -4.2297, -1.0214, 3.3802, 57.3576},
                        {325, -13.237, -12.9762, -12.3511, -11.1658, -9.4066, -7.011, -3.8653, 0.3748, 56.6623},
                        {350, -15.8116, -15.5606, -14.9452, -13.772, -12.0286, -9.6574, -6.5596, -2.4442, 56.0186},
                        {375, -18.2844, -18.041, -17.433, -16.2693, -14.538, -12.1858, -9.1254, -5.1077, 55.4194},
                        {400, -20.668, -20.4306, -19.8285, -18.6721, -16.9504, -14.6132, -11.5823, -7.6423, 54.8588},
                        {425, -22.976, -22.7433, -22.1459, -20.9954, -19.2814, -16.9562, -13.9489, -10.0715, 54.3322},
                        {450, -25.2221, -24.9933, -24.3996, -23.2539, -21.5462, -19.2307, -16.2426, -12.4161, 53.8358},
                        {475, -27.42, -27.1944, -26.6038, -25.462, -23.7594, -21.4518, -18.4795, -14.695, 53.3661},
                        {500, -29.5828, -29.3598, -28.7718, -27.6332, -25.9348, -23.6338, -20.6746, -16.9249, 52.9206},
                        {525, -31.7227, -31.5019, -30.916, -29.7801, -28.0852, -25.7898, -22.8414, -19.1209, 52.4968},
                        {550, -33.8508, -33.6318, -33.0477, -31.9142, -30.2222, -27.9314, -24.9923, -21.2964, 52.0927},
                        {575, -35.977, -35.7596, -35.1771, -34.0454, -32.356, -30.0691, -27.1378, -23.463, 51.7066},
                        {600, -38.1099, -37.8939, -37.3126, -36.1826, -34.4953, -32.2118, -29.2872, -25.6303, 51.337},
                        {625, -40.2563, -40.0414, -39.4613, -38.3327, -36.6473, -34.3667, -31.4479, -27.8064, 50.9824},
                        {650, -42.4214, -42.2075, -41.6283, -40.501, -38.8172, -36.5391, -33.6253, -29.9973, 50.6417},
                        {675, -44.6085, -44.3955, -43.8171, -42.6908, -41.0085, -38.7325, -35.8231, -32.2068, 50.3139},
                        {700, -46.8189, -46.6066, -46.029, -44.9037, -43.2225, -40.9485, -38.0429, -34.4368, 49.998},
                        {725, -49.0519, -48.8403, -48.2634, -47.1388, -45.4587, -43.1864, -40.2841, -36.6871, 49.6932},
                        {750, -51.3047, -51.0937, -50.5173, -49.3935, -47.7144, -45.4436, -42.5442, -38.9551, 49.3988},
                        {775, -53.5724, -53.3619, -52.786, -51.6629, -49.9846, -47.7151, -44.8183, -41.2363, 49.114},
                        {800, -55.848, -55.638, -55.0626, -53.94, -52.2625, -49.9941, -47.0997, -43.524, 48.8382},
                        {825, -58.1228, -57.9133, -57.3383, -56.2162, -54.5393, -52.272, -49.3797, -45.8096, 48.5709},
                        {850, -60.3864, -60.1772, -59.6026, -58.481, -56.8047, -54.5384, -51.6479, -48.0829, 48.3116},
                        {875, -62.627, -62.4181, -61.8438, -60.7226, -59.0469, -56.7814, -53.8926, -50.3321, 48.0598},
                        {900, -64.8316, -64.6231, -64.049, -62.9282, -61.253, -58.9882, -56.101, -52.5446, 47.8152},
                        {925, -66.9871, -66.7788, -66.205, -65.0846, -63.4097, -61.1457, -58.2598, -54.7071, 47.5772},
                        {950, -69.0798, -68.8717, -68.2982, -67.178, -65.5036, -63.2402, -60.3556, -56.8063, 47.3455},
                        {975, -71.0967, -70.8889, -70.3156, -69.1957, -67.5216, -65.2588, -62.3753, -58.829, 47.1199},
                        {1000, -73.0257, -72.8181, -72.245, -71.1254, -69.4516, -67.1893, -64.3068, -60.7634, 46.9},
                }
        };


        double h2 = 10;
        double ha = 0;
        double hb = 0;

        String[] path = {"Land", "Land", "Land", "Sea", "Cold", "Cold", "Warm", "Warm", "Land", "Land", "Land", "Sea", "Cold", "Cold", "Warm", "Warm", "Land", "Land", "Land", "Sea", "Cold", "Cold", "Warm", "Warm"};

        double[] f = {100, 100, 100, 100, 100, 100, 100, 100, 600, 600, 600, 600, 600, 600, 600, 600, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000};

        double[] t = {50, 10, 1, 50, 10, 1, 10, 1, 50, 10, 1, 50, 10, 1, 10, 1, 50, 10, 1, 50, 10, 1, 10, 1};

        double R1 = -1; // no clutter
        double R2 = 10;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double tca = 91;
        double htter = 0;
        double hrter = 0;
        double eff1 = 91;
        double eff2 = 91;
        double q = 50;
        double PTx = 1;
        int pathinfo = 1;
        double wa = 0;
        int debug = 0;
        int fidlog = 0;
        double sigmaL = 0;

        //for (int ii=1; ii <= t.length; ii++){
        for (int ii = 9; ii <= 9; ii++) {


            double[][] dummy = new double[79][10];

            for (int kk = 0; kk < 79; kk++) {
                for (int ll = 0; ll < 10; ll++) {
                    dummy[kk][ll] = excelTables[0][kk][ll];
                }
            }

            double[] h1 = new double[8];

            for (int ll = 1; ll < 9; ll++) {
                h1[ll - 1] = dummy[0][ll];
            }

            double[] d = new double[78];

            for (int ll = 1; ll < 79; ll++) {
                d[ll - 1] = dummy[ll][0];
            }


            for (int jj = 0; jj < h1.length; jj++) {
                //for (int jj = 3; jj <= 3; jj++) {


                for (int kk = d.length - 1; kk < d.length; kk++) {
                    double[] d_v = {d[kk]};
                    String[] path_c = {path[ii]};
                    double L = calculator.P1546FieldStrMixed(f[ii], t[ii], h1[jj], h2, R2, area, d_v, path_c, pathinfo,
                            q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);

                    double Em = dummy[kk + 1][jj + 1];

                    double Lm = 139.3 - Em + 20 * Math.log10(f[ii]);

                    double Es = 139.3 - L + 20 * Math.log10(f[ii]);

                    /*
                    System.out.printf("\n");


                    System.out.printf("f   =   %f\n", f[ii]);
                    System.out.printf("t   =   %f\n", t[ii]);
                    System.out.printf("R2   =   %f\n", R2);
                    System.out.printf("h1   =  %f\n", h1[jj]);
                    System.out.printf("d   =   %f\n", d[kk]);

                    System.out.printf("Em   =  %f\n", Em);
                    System.out.printf("Es   =  %f\n", Es);
                    System.out.printf("Lm   =  %f\n", Lm);
                    System.out.printf("Ls   =  %f\n", L);

                */
                    Assert.assertEquals(Em, Es, 1e-3);

                }
            }
        }
    }


    @Test
    public void test19() {
        // Figure 1
        // In this test the Figures 1-24 from ITU-R P1546-4 are reproduced and
        // compared with the original data extracted from excel tables.

        P1546 calculator = new P1546();
        //// Data extracted from the Excel tables


        double[][][] excelTables = new double[][][]{
                {
                        {78, 10, 20, 37.5, 75, 150, 300, 600, 1200, 0},
                        {1, 89.9759, 92.1812, 94.6355, 97.3845, 100.3181, 103.1205, 105.2426, 106.3566, 106.9},
                        {2, 80.2751, 83.0908, 86.0014, 89.2076, 92.6742, 96.1197, 98.8577, 100.2846, 100.8794},
                        {3, 74.1662, 77.5296, 80.8234, 84.3504, 88.1427, 91.9686, 95.0958, 96.7306, 97.3576},
                        {4, 69.5184, 73.3548, 77.0149, 80.8312, 84.885, 88.9934, 92.4125, 94.2077, 94.8588},
                        {5, 65.6994, 69.9206, 73.9248, 78.0214, 82.3137, 86.6601, 90.3203, 92.2498, 92.9206},
                        {6, 62.4359, 66.9578, 71.2723, 75.6407, 80.1635, 84.7271, 88.6005, 90.6489, 91.337},
                        {7, 59.5803, 64.3322, 68.9161, 73.5423, 78.2915, 83.0633, 87.1352, 89.294, 89.998},
                        {8, 57.0412, 61.9673, 66.7783, 71.6424, 76.6127, 81.5891, 85.8531, 88.1186, 88.8382},
                        {9, 54.756, 59.814, 64.8127, 69.8903, 75.0733, 80.2524, 84.7074, 87.0797, 87.8152},
                        {10, 52.6796, 57.8377, 62.9896, 68.2548, 73.6382, 79.0175, 83.6656, 86.1477, 86.9},
                        {11, 50.7782, 56.0126, 61.2886, 66.7156, 72.2842, 77.8593, 82.704, 85.3015, 86.0721},
                        {12, 49.0255, 54.3183, 59.6945, 65.2592, 70.9957, 76.7598, 81.8047, 84.5251, 85.3164},
                        {13, 47.4007, 52.7385, 58.1954, 63.8762, 69.7625, 75.7062, 80.9539, 83.8065, 84.6211},
                        {14, 45.8873, 51.2596, 56.7816, 62.5595, 68.5777, 74.6895, 80.141, 83.1361, 83.9774},
                        {15, 44.4715, 49.8704, 55.4448, 61.3035, 67.4365, 73.7034, 79.3576, 82.5061, 83.3782},
                        {16, 43.1423, 48.5613, 54.1779, 60.1035, 66.3356, 72.7438, 78.5971, 81.9102, 82.8176},
                        {17, 41.8902, 47.3243, 52.9746, 58.9555, 65.2725, 71.8079, 77.8546, 81.3432, 82.291},
                        {18, 40.7074, 46.1523, 51.8294, 57.8559, 64.2452, 70.894, 77.1264, 80.8006, 81.7946},
                        {19, 39.5871, 45.0395, 50.7377, 56.8014, 63.2518, 70.0011, 76.4098, 80.2786, 81.3249},
                        {20, 38.5237, 43.9806, 49.695, 55.7889, 62.291, 69.1285, 75.7029, 79.7742, 80.8794},
                        {25, 33.9069, 39.3532, 45.097, 51.2676, 57.9231, 65.0574, 72.2902, 77.4302, 78.9412},
                        {30, 30.1811, 35.5753, 41.2901, 47.4593, 54.1611, 61.4361, 69.0821, 75.247, 77.3576},
                        {35, 27.1022, 32.4093, 38.0527, 44.1712, 50.8594, 58.1943, 66.0991, 73.1376, 76.0186},
                        {40, 24.5178, 29.7039, 35.239, 41.2678, 47.9017, 55.2511, 63.3319, 71.0823, 74.8588},
                        {45, 22.3242, 27.3561, 32.7481, 38.6526, 45.199, 52.5325, 60.7465, 69.0829, 73.8358},
                        {50, 20.4457, 25.2917, 30.5082, 36.2563, 42.6853, 49.9783, 58.3013, 67.139, 72.9206},
                        {55, 18.8242, 23.456, 28.4679, 34.0304, 40.3142, 47.5432, 55.9575, 65.2431, 72.0927},
                        {60, 17.4138, 21.8079, 26.5907, 31.9423, 38.0553, 45.1964, 53.6839, 63.3818, 71.337},
                        {65, 16.1775, 20.3164, 24.8512, 29.9716, 35.891, 42.9195, 51.4583, 61.5403, 70.6417},
                        {70, 15.0848, 18.9575, 23.232, 28.1062, 33.813, 40.7044, 49.2674, 59.7052, 69.998},
                        {75, 14.1104, 17.7128, 21.721, 26.3401, 31.8196, 38.5503, 47.1059, 57.8662, 69.3988},
                        {80, 13.2334, 16.5674, 20.3094, 24.6703, 29.9126, 36.4612, 44.9742, 56.017, 68.8382},
                        {85, 12.4361, 15.5089, 18.9902, 23.0948, 28.0952, 34.4436, 42.8775, 54.1554, 68.3116},
                        {90, 11.7041, 14.5267, 17.757, 21.612, 26.3701, 32.5044, 40.8233, 52.2826, 67.8152},
                        {95, 11.0249, 13.6116, 16.6037, 20.2192, 24.7389, 30.6498, 38.8202, 50.4029, 67.3455},
                        {100, 10.3885, 12.7552, 15.5241, 18.9127, 23.2014, 28.8839, 36.8766, 48.5226, 66.9},
                        {110, 9.2115, 11.1888, 13.56, 16.5389, 20.3971, 25.6248, 33.1954, 44.7914, 66.0721},
                        {120, 8.121, 9.775, 11.8136, 14.4442, 17.9235, 22.7202, 29.8173, 41.1534, 65.3164},
                        {130, 7.0818, 8.4718, 10.2372, 12.578, 15.733, 20.1389, 26.7509, 37.666, 64.6211},
                        {140, 6.0704, 7.2464, 8.7898, 10.8924, 13.7749, 17.8372, 23.9815, 34.3703, 63.9774},
                        {150, 5.0717, 6.0747, 7.4382, 9.3465, 12.0024, 15.7687, 21.4807, 31.2885, 63.3782},
                        {160, 4.0764, 4.9392, 6.157, 7.9069, 10.3756, 13.8901, 19.2138, 28.4266, 62.8176},
                        {170, 3.0791, 3.8278, 4.9273, 6.5481, 8.8623, 12.1644, 17.1458, 25.7785, 62.291},
                        {180, 2.0772, 2.7325, 3.7355, 5.2507, 7.4373, 10.5609, 15.2444, 23.3302, 61.7946},
                        {190, 1.0699, 1.6483, 2.5723, 4.0007, 6.0818, 9.0554, 13.4815, 21.0635, 61.3249},
                        {200, 0.0578, 0.5723, 1.4312, 2.7879, 4.7814, 7.629, 11.8338, 18.9592, 60.8794},
                        {225, -2.4856, -2.0889, -1.3489, -0.1229, 1.7093, 4.3216, 8.101, 14.2872, 59.8564},
                        {250, -5.0264, -4.7076, -4.0446, -2.9035, -1.1768, 1.2793, 4.7672, 10.2631, 58.9412},
                        {275, -7.539, -7.2732, -6.6619, -5.5778, -3.922, -1.5725, 1.7131, 6.7063, 58.1133},
                        {300, -10.0034, -9.7747, -9.1991, -8.1543, -6.5478, -4.2726, -1.1303, 3.4955, 57.3576},
                        {325, -12.4069, -12.2047, -11.6545, -10.6375, -9.0661, -6.844, -3.8052, 0.5494, 56.6623},
                        {350, -14.7435, -14.5606, -14.0287, -13.0321, -11.4862, -9.303, -6.3403, -2.1885, 56.0186},
                        {375, -17.0125, -16.844, -16.3258, -15.3442, -13.8173, -11.6631, -8.7573, -4.7594, 55.4194},
                        {400, -19.2175, -19.0598, -18.5518, -17.5816, -16.0691, -13.9369, -11.0746, -7.1948, 54.8588},
                        {425, -21.3647, -21.2152, -20.7152, -19.7537, -18.2523, -16.137, -13.3083, -9.5205, 54.3322},
                        {450, -23.4623, -23.3193, -22.8254, -21.8708, -20.378, -18.276, -15.4736, -11.7583, 53.8358},
                        {475, -25.5199, -25.3819, -24.8928, -23.9436, -22.4577, -20.3662, -17.5847, -13.9273, 53.3661},
                        {500, -27.5473, -27.4133, -26.9281, -25.9832, -24.5028, -22.4197, -19.655, -16.0441, 52.9206},
                        {525, -29.5545, -29.4237, -28.9417, -28.0003, -26.5243, -24.448, -21.6969, -18.1239, 52.4968},
                        {550, -31.5512, -31.423, -30.9435, -30.005, -28.5327, -26.462, -23.722, -20.1801, 52.0927},
                        {575, -33.5464, -33.4204, -32.943, -32.0069, -30.5376, -28.4715, -25.7407, -22.2245, 51.7066},
                        {600, -35.5483, -35.424, -34.9484, -34.0142, -32.5474, -30.4851, -27.762, -24.2673, 51.337},
                        {625, -37.5636, -37.4409, -36.9667, -36.0342, -34.5695, -32.5104, -29.7938, -26.317, 50.9824},
                        {650, -39.5981, -39.4765, -39.0036, -38.0725, -36.6095, -34.5532, -31.842, -28.3805, 50.6417},
                        {675, -41.6555, -41.5349, -41.0631, -40.1331, -38.6717, -36.6177, -33.9111, -30.4626, 50.3139},
                        {700, -43.738, -43.6183, -43.1474, -42.2184, -40.7582, -38.7062, -36.0036, -32.5662, 49.998},
                        {725, -45.8459, -45.727, -45.2568, -44.3287, -42.8696, -40.8193, -38.12, -34.6922, 49.6932},
                        {750, -47.9774, -47.8591, -47.3896, -46.4623, -45.0042, -42.9553, -40.259, -36.8394, 49.3988},
                        {775, -50.1288, -50.0111, -49.5421, -48.6155, -47.1582, -45.1106, -42.4168, -39.0044, 49.114},
                        {800, -52.2942, -52.177, -51.7086, -50.7825, -49.3259, -47.2794, -44.5879, -41.1817, 48.8382},
                        {825, -54.4659, -54.3492, -53.8812, -52.9555, -51.4996, -49.4541, -46.7645, -43.3639, 48.5709},
                        {850, -56.6344, -56.518, -56.0504, -55.1252, -53.6698, -51.6252, -48.9373, -45.5415, 48.3116},
                        {875, -58.7885, -58.6724, -58.2052, -57.2804, -55.8255, -53.7816, -51.0952, -47.7037, 48.0598},
                        {900, -60.916, -60.8002, -60.3332, -59.4088, -57.9543, -55.9111, -53.2261, -49.8383, 47.8152},
                        {925, -63.0036, -62.888, -62.4213, -61.4972, -60.0431, -58.0005, -55.3167, -51.9323, 47.5772},
                        {950, -65.0378, -64.9225, -64.456, -63.5322, -62.0784, -60.0363, -57.3536, -53.9722, 47.3455},
                        {975, -67.0053, -66.8902, -66.4239, -65.5003, -64.0469, -62.0052, -59.3234, -55.9447, 47.1199},
                        {1000, -68.8933, -68.7783, -68.3123, -67.3889, -65.9357, -63.8945, -61.2136, -57.8373, 46.9},
                }

        };


        double h2 = 10;
        double ha = 0;
        double hb = 0;

        String[] path = {"Land", "Land", "Land", "Sea", "Cold", "Cold", "Warm", "Warm", "Land", "Land", "Land", "Sea", "Cold", "Cold", "Warm", "Warm", "Land", "Land", "Land", "Sea", "Cold", "Cold", "Warm", "Warm"};

        double[] f = {100, 100, 100, 100, 100, 100, 100, 100, 600, 600, 600, 600, 600, 600, 600, 600, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000};

        double[] t = {50, 10, 1, 50, 10, 1, 10, 1, 50, 10, 1, 50, 10, 1, 10, 1, 50, 10, 1, 50, 10, 1, 10, 1};

        double R1 = -1; // no clutter
        double R2 = 10;
        P1546.ClutterEnvironment area = P1546.ClutterEnvironment.RURAL;
        double tca = 91;
        double htter = 0;
        double hrter = 0;
        double eff1 = 91;
        double eff2 = 91;
        double q = 50;
        double PTx = 1;
        int pathinfo = 1;
        double wa = 0;
        int debug = 0;
        int fidlog = 0;
        double sigmaL = 0;

        //for (int ii=1; ii <= t.length; ii++){
        for (int ii = 0; ii <= 0; ii++) {


            double[][] dummy = new double[79][10];

            for (int kk = 0; kk < 79; kk++) {
                for (int ll = 0; ll < 10; ll++) {
                    dummy[kk][ll] = excelTables[0][kk][ll];
                }
            }

            double[] h1 = new double[8];

            for (int ll = 1; ll < 9; ll++) {
                h1[ll - 1] = dummy[0][ll];
            }

            double[] d = new double[78];

            for (int ll = 1; ll < 79; ll++) {
                d[ll - 1] = dummy[ll][0];
            }


            for (int jj = 0; jj < h1.length; jj++) {
                //for (int jj = 3; jj <= 3; jj++) {


                for (int kk = d.length - 1; kk < d.length; kk++) {
                    double[] d_v = {d[kk]};
                    String[] path_c = {path[ii]};
                    double L = calculator.P1546FieldStrMixed(f[ii], t[ii], h1[jj], h2, R2, area, d_v, path_c, pathinfo,
                            q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);

                    double Em = dummy[kk + 1][jj + 1];

                    double Lm = 139.3 - Em + 20 * Math.log10(f[ii]);

                    double Es = 139.3 - L + 20 * Math.log10(f[ii]);

                        /*
                        System.out.printf(     "\n"  );


                        System.out.printf(     "f   =   %f\n"  ,f[ii]);
                        System.out.printf(     "t   =   %f\n"  ,t[ii]);
                        System.out.printf(     "R2   =   %f\n"  ,R2);
                        System.out.printf(     "h1   =  %f\n"  ,h1[jj]);
                        System.out.printf(     "d   =   %f\n"  ,d[kk]);

                        System.out.printf(     "Em   =  %f\n"  ,Em);
                        System.out.printf(     "Es   =  %f\n"  ,Es);
                        System.out.printf(     "Lm   =  %f\n"  ,Lm);
                        System.out.printf(     "Ls   =  %f\n"  ,L);
                        */

                    Assert.assertEquals(Em, Es, 1e-3);

                }
            }
        }
    }

}




