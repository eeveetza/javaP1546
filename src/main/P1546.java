package main;

// Recommendation ITU-R P.1546

public class P1546 {
    //
    // Class implementation of Recommendation ITU-R P.1546-6
    //
    //
    //  Copyright (c) 2017- , Ivica Stevanovic
    //  All rights reserved.
    //
    // Redistribution and use in source and binary forms, with or without
    // modification, are permitted provided that the following conditions are
    // met:
    //
    //     * Redistributions of source code must retain the above copyright
    //       notice, this list of conditions and the following disclaimer.
    //     * Redistributions in binary form must reproduce the above copyright
    //       notice, this list of conditions and the following disclaimer in
    //       the documentation and/or other materials provided with the distribution
    //
    //
    ////
    // THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    // AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    // IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    // ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    // LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    // CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    // SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    // INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    // CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    // ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    // POSSIBILITY OF SUCH DAMAGE.
    //
    // THE AUTHORS AND OFCOM (CH) DO NOT PROVIDE ANY SUPPORT FOR THIS SOFTWARE
    ////


    public enum ClutterEnvironment {

        NONE("No clutter"),
        WATER("Water/Sea"),
        URBAN("Urban"),
        URBAN_MICRO("Urban Micro Cell"),
        SUBURBAN("Suburban"),
        DENSE_SUBURBAN("Dense Suburban"),
        RURAL("Rural"),
        DENSE_URBAN("Dense Urban"),
        HIGH_RISE_URBAN("High-rise Urban"),
        RESIDENTIAL("Residential"),
        INDUSTRIAL("Industrial zone"),
        USER_SPECIFIED("User specified");
        private String name;
        ClutterEnvironment(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public double P1546FieldStrMixed(double f, double t, double heff, double h2, double R2, ClutterEnvironment area, double[] d_v, String[] path_c,
                                     int pathinfo, double q, double PTx, double ha, double hb, double R1, double tca,
                                     double htter, double hrter, double eff1, double eff2, double sigma_L) {
        // P1546FieldStrMixed: ITU 1546-6 (2019) Field strength calculator
        //
        //   L = P1546FieldStrMixed(f,t,heff,h2,R2,area,d_v,path_c,pathinfo,
        //                          q,PTx,ha,hb,R1,tca,htter,hrter,eff1,eff2, sigmaL);
        //
        // where:    Units,  Definition                             Limits
        // f:        MHz     Required frequency                     30 MHz - 4000 MHz
        // t:        %      Required percentage time                1 % - 50 %
        // heff:     m       Effective height of the
        //                   transmitting/base antenna, height over
        //                   the average level of the ground between
        //                   distances of 3 and 15 km from the
        //                   transmitting/base antenna in the
        //                   direction of the receiving/mobile antenna.
        // h2:       m       Receiving/mobile antenna height above ground
        // R2:       m       Representative clutter height around receiver
        //                   Typical values:
        //                   R2=10 for area='Rural' or 'Suburban' or 'Sea'
        //                   R2=15 for area='Urban'
        //                   R2=20 for area='Dense Urban'
        // area:             Area around the receiver   ClutterEnvironment.RURAL, URBAN, SUBURBAN
        //                                                          DENSE_URBAN
        //                                                          WATER, NONE
        // d_v:      km      Vector of horizontal path lengths       d=sum(d_v) <= 1000 km
        //                   over different path zones starting
        //                   from transmitter/base terminal
        // path_c:   string  Cell of strings defining the path        'Land', 'Sea',
        //                   zone for each given path length in d_v   'Warm', 'Cold'
        //                   starting from transmitter/base terminal
        // pathinfo: 0/1     0 - no terrain profile information available,
        //                   1 - terrain information available
        // q:        %       Location variability (default 50//)      1% - 99%
        // PTx:      kW      Transmitter (e.r.p) power in kW (default 1 kW)
        // ha:       m       Transmitter antenna height above        > 1 m
        //                   ground. Defined in Annex 5 sec 3.1.1.
        //                   Limits are defined in Annex 5 sec 3.
        // hb:       m       Height of transmitter/base antenna
        //                   above terrain height averaged
        //                   0.2 d and d km, where d is less than
        //                   15 km and where terrain information
        //                   is available.
        // R1:       m       Representative clutter height around transmitter
        // tca:      deg     Terrain clearance angle                 0.55 - 40 deg
        // htter:    m       Terrain height in meters above
        //                   sea level at the transmitter/base
        // hrter:    m       Terrain height in meters above
        //                   sea level at the receiver/mobile
        // eff1:     deg     The h1 terminal's terrain clearance
        //                   angle calculated using the method in
        //                   Paragraph 4.3 case a), whether or not h1 is
        //                   negative
        // eff2:     deg     The h2 terminal's clearance angle
        //                   as calculated in Section 11, noting that
        //                   this is the elevation angle relative to
        //                   the local horizontal
        // sigma_L   dB      Standard deviation of the Gauss distribution of the local means in the study area (Annex 5, Section 12)
        //
        // Output variables:
        //
        // L:        dB      Path loss
        //
        // This function implements ITU-R P.1546-6 recommendation,
        // describing a method for point-to-area radio propagation predictions for
        // terrestrial services in the frequency range 30 MHz to 3000 MHz. It is
        // intended for use on tropospheric radio circuits over land paths, sea paths,
        // and/or mixed land-sea paths up to 1000 km length for effective
        // transmitting antenna heights less than 3000 m. The method is based on
        // interpolation/extrapolation from empirically derived field-strength
        // curves as functions of distance, antenna height, frequency, and percentage
        // time. The calculation procedure also includes corrections to the results
        // obtained from this interpolation/extrapolation to account for terrain
        // clearance, terminal clutter obstructions, and location variability.
        //
        // Notes:
        //
        // Not implemented in this version of the code:
        //  - Annex 7: Adjustment for different climatic regions
        //  - Annex 5, Section 4.3a): C_h1 calculation (terrain database is
        //  available and the potential of discontinuities around h1 = 0 is of no
        //  concern)
        //
        // How to use:
        //
        //   E = P1546FieldStrMixed(f,t,heff,h2,R2,area,d_v,path_c,pathinfo,
        //                          q,PTx,ha,hb,R1,tca,htter,hrter,eff1,eff2,sigmaL);
        //
        // Numbers refer to Rec. ITU-R P.1546-6

        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    21DEC16     Ivica Stevanovic, OFCOM         Initial implementation in Java
        // v2    31OCT19     Ivica Stevanovic, OFCOM         Aligned with ITU-R P.1546-6
        //                                                   Steps 1-16 use d = 1 km for 0.04 < d < 1 km
        // v3    21FEB20     Ivica Stevanovic, OFCOM         Introduced changes in conditions for applying corrections related to R1, R2, h1, and tca
        //  Copyright (c) 2016-2020, Ivica Stevanovic
        //  All rights reserved.
        //
        //
        // THE AUTHORS AND OFCOM (CH) DO NOT PROVIDE ANY SUPPORT FOR THIS SOFTWARE
        ////


        //// Read the input arguments and check them

        // Checking passed parameter to the defined limits

        //limit(f, 30, 4000, "f");
        limit(f, 30, 1e10, "f"); // to allow frequencies above 3 GHz without raising an exception

        limit(t, 1, 50, "t");

        // limit(heff, heff, 3000, "heff");
        limit(heff, heff, 1e10, "heff"); // to allow Tx effective heights above 3000 m without raising an exception

        // compute the total path
        double d = 0;

        for (int i = 0; i < d_v.length; i++) {
            d = d + d_v[i];
        }
        // limit(d, 0, 1000, "d");
        limit(d, 0, 1e10, "d"); // to allow distances above 1000 km without raising an exception

        int NN = d_v.length;


        // Optional arguments
        // 3 Determination of transmitting/base antenna height, h1
        // In case of mixed paths, h1 should be calculated using Annex 5, sec. 3
        // taking the height of any sea surface as though land. Normally this value
        // of h1 will be used for both Eland(d) and Esea(d).
        // HOWEVER; if h1 < 3m it should
        // be used as such for Eland, but a value of 3 m should be used for Esea(d)

        int path = 1;

        if (NN > 1) { // mixed paths
            path = 1;
        } else { // single path zone, Land or [Cold or Warm] Sea
            if (path_c[0].equalsIgnoreCase("Land")) {
                path = 1;
            } else if (path_c[0].equalsIgnoreCase("Warm")) {
                path = 2;
            } else {
                path = 3;
            }
        }
        double h1 = h1Calc(d, heff, ha, hb, path, pathinfo);

        if (h1 > 3000) {
            h1 = 3000;
        }


        // Step 1: Determine the type of the propagation path as land, cold sea or
        // warm sea. If the path is mixed then determine two path types which are
        // regarded as first and second propagation types. If the path can be
        // represented by a single type then this is regarded as the first
        // propagation type and the mixed-path method given in Step 11 is not
        // required.
        //   Where,
        //   time is percentage 1%,10%,50%
        //   path is 1 = Land, 2 = Warm Sea, 3 = Cold Sea

        // Step 2: For any given percentage of time (in the range 1% to 50% time)
        // determine two nominal time percentages as follows:
        // – wanted time percentage > 1 and < 10, the lower and higher nominal
        //   percentages are 1 and 10, respectively;
        // – wanted time percentage > 10 and < 50, the lower and higher nominal
        //   percentages are 10 and 50, respectively.
        // If the required percentage of time is equal to 1% or 10% or 50%, this
        // value should be regarded as the lower nominal percentage time and the
        // interpolation process of Step 10 is not required.

        //        done in start of step6_10()

        // Step 3: For any wanted frequency (in the range 30 to 3 000 MHz) determine
        // two nominal frequencies as follows:
        // – where the wanted frequency < 600 MHz, the lower and higher nominal
        //   frequencies are 100 and 600 MHz, respectively;
        // – where the wanted frequency > 600 MHz, the lower and higher nominal
        //   frequencies are 600 and 2 000 MHz, respectively.
        // If the wanted frequency equals 100 or 600 or 2 000 MHz, this value should
        // be regarded as the lower nominal frequency and the
        // interpolation/extrapolation process of Step 9 is not required.

        //       done in start of step7_normal(): step 7-9

        // Step 4: Determine the lower and higher nominal distances from Table 1
        // closest to the required distance. If the required distance coincides with
        // a value in Table 1, this should be regarded as the lower nominal distance
        // and the interpolation process of Step 8.1.5 is not required.

        double[] dinfsup = FindDNominals(d);
        double dinf = dinfsup[0];
        double dsup = dinfsup[1];

        // In case the field needs to be computed for distances < 1 km
        if (d < 1) {
            dinf = 1;
            dsup = 1;
        }

        int nl = 0;
        int ns = 0;

        for (int ii = 0; ii < NN; ii++) { //

            if (path_c[ii].equalsIgnoreCase("Land")) {
                nl = nl + 1;
            } else {
                ns = ns + 1;
            }
        }


        double[] El = new double[nl];
        double[] dl = new double[nl];
        double[] Es = new double[ns];
        double[] ds = new double[ns];
        double dlsum = 0;
        double dssum = 0;
        int cland = 0;
        int csea = 0;
        for (int ii = 0; ii < NN; ii++) { //

            if (path_c[ii].equalsIgnoreCase("Land")) {
                dl[cland] = d_v[ii];
                cland = cland + 1;
                dlsum = dlsum + d_v[ii];
            } else {
                ds[csea] = d_v[ii];
                csea = csea + 1;
                dssum = dssum + d_v[ii];
            }
        }


        // Compute the maximum value of the field strength as given in Annex 5, Sec.
        // 2 for the case of mixed path
        // In case, the slope path correction is necessary for the calculation of
        // Emax

        double EmaxF = Step_19a(t, dlsum, dssum);

        // Step 16: Apply the slope-path correction given in annex 5, Sec. 14
        EmaxF = EmaxF + Step_16a(ha, h2, d, htter, hrter);

        cland = 0;
        csea = 0;
        for (int ii = 0; ii < NN; ii++) {

            // Step 5: For each propagation type follow Steps 6 to 10.

            if (path_c[ii].equalsIgnoreCase("Land")) {
                path = 1;
            } else if (path_c[ii].equalsIgnoreCase("Warm")) {
                path = 2;
            } else { // "Cold"
                path = 3;
            }
            double Epath = 0.0;
            if (d >= 1) {
                Epath = step6_10(t, f, h1, path, d, EmaxF);
            } else {
                Epath = step6_10(t, f, h1, path, 1.0, EmaxF);
            }
            if (path == 1) {
                El[cland] = Epath;
                cland = cland + 1;
            } else {
                Es[csea] = Epath;
                csea = csea + 1;
            }
        }
        // Step 11: If the prediction is for a mixed path, follow the step-by-step
        // procedure given in Annex 5, § 8. This requires use of Steps 6 to 10 for
        // paths of each propagation type. Note that if different sections of the
        // path exist classified as both cold and warm sea, all sea sections should
        // be classified as warm sea.

        double E = Step_11a_rrc06(El, Es, dl, ds);

        // Step 12: If information on the terrain clearance angle at a
        // receiving/mobile antenna adjacent to land is available, correct the field
        // strength for terrain clearance angle at the receiver/mobile using the
        // method given in Annex 5, Sec. 11.
        if (Math.abs(tca) < 90) {
            double[] tca_corr;
            tca_corr = Step_12a(f, tca);
            double Correction = tca_corr[0];
            double nu = tca_corr[1];
            E = E + Correction;
        }


        // Step 13: Calculate the estimated field strength due to tropospheric
        // scattering using the method given in Annex 5, Sec. 13 and take the
        // maximum of E and Ets.
        if ((Math.abs(eff1) < 90) && (Math.abs(eff2) < 90)) {
            //disp('13: Calculating correction due to tropospheric scattering')
            double[] tscat_corr;
            if (d >= 1) {
                tscat_corr = Step_13a(d, f, t, eff1, eff2);
            } else {
                tscat_corr = Step_13a(1.0, f, t, eff1, eff2);
            }
            double Ets = tscat_corr[0];
            double theta_s = tscat_corr[1];
            E = Math.max(E, Ets);
        }

        // Step 14: Correct the field strength for receiving/mobile antenna height
        // h2 using the method given in Annex 5, Sec. 9

        if (path_c[NN - 1].equalsIgnoreCase("Land")) {
            path = 1;
        } else if (path_c[NN - 1].equalsIgnoreCase("Warm")) {
            path = 2;
        } else { // "Cold"
            path = 3;
        }

        double[] st14corr;
        if (d >= 1.0) {
            st14corr = Step_14a(h1, d, R2, h2, f, area);
        } else {
            st14corr = Step_14a(h1, 1.0, R2, h2, f, area);
        }
        double Correction = st14corr[0];
        double R2p = st14corr[1];
        E = E + Correction;


        // Step 15: If there is clutter around the transmitting/base terminal, even
        // if at lower height above ground than the antenna, correct for its effect
        // using the  method given in Annex 5, Sec. 10
        // If Tx in open/uncluttered (R1 = -1), do not compute the correction
        if (ha > 0 && R1 > 0) {
            E = E + Step_15a(ha, R1, f);
        }

        // Step 16: Apply the slope-path correction given in annex 5, Sec. 14
        if (ha > 0 && h2 < 10000) {
            if (d >= 1.0) {
                E = E + Step_16a(ha, h2, d, htter, hrter);
            } else {
                E = E + Step_16a(ha, h2, 1.0, htter, hrter);
            }
        }

        // Step 17: // In case the path is less than 1 km
        if (d < 0.9999999999) {
            E = Step_17a(ha, h2, d, E, htter, hrter);
        }


        // Step 18: Correct the field strength for the required percentage of
        // locations using the method given in Annex 5, Sec. 12.

        if ((q != 50) /*&& (area != Sea)*/) {
            E = Step_18a(E, q, sigma_L);
            double Edebug = E;
        }

        // Step 19: If necessary, limit the resulting field strength to the maximum
        // given in Annex 5, Sec. 2. If a mixed path calculation has been made for a
        // percentage time less than 50// use the method given by (42)

        if (E > EmaxF) {
            //disp('19: Limitting the maximum value of the field strength.')
            E = EmaxF;
        }


        // Step 20: If required, convert field strength to eqivalent basic
        // transmission loss for the path using the method given in Annex 5, Sec 17
        // for 1 kW

        double L = Step_20a(f, E);

        // Scale to the transmitter power

        E = E + 10 * Math.log10(PTx);


        return L;
    }

    public static double[][] tabIndex = new double[][]{
            {1, 10, 50},
            {100, 600, 2000},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 450, 475, 500, 525, 550, 575, 600, 625, 650, 675, 700, 725, 750, 775, 800, 825, 850, 875, 900, 925, 950, 975, 1000,},
            {10, 20, 37.5, 75, 150, 300, 600, 1200},
    };

    private static final class tabData {

        private static final double[][][][] get = new double[][][][]{
                {
                        {// 100 MHz 1%
                                {89.976, 92.181, 94.636, 97.385, 100.318, 103.121, 105.243, 106.357, 106.900},
                                {80.275, 83.091, 86.080, 89.407, 92.913, 96.331, 98.981, 100.326, 100.879},
                                {74.166, 77.530, 80.898, 84.623, 88.495, 92.298, 95.296, 96.797, 97.358},
                                {69.518, 73.355, 77.049, 81.110, 85.279, 89.384, 92.660, 94.289, 94.859},
                                {65.699, 69.921, 73.942, 78.287, 82.714, 87.077, 90.596, 92.340, 92.921},
                                {62.436, 66.958, 71.318, 75.901, 80.555, 85.147, 88.888, 90.743, 91.337},
                                {59.648, 64.332, 69.040, 73.818, 78.672, 83.473, 87.424, 89.390, 89.998},
                                {57.462, 62.166, 67.024, 71.961, 76.990, 81.983, 86.135, 88.212, 88.838},
                                {55.541, 60.276, 65.216, 70.280, 75.462, 80.630, 84.977, 87.170, 87.815},
                                {53.831, 58.580, 63.577, 68.742, 74.055, 79.383, 83.920, 86.232, 86.900},
                                {52.292, 57.043, 62.078, 67.322, 72.747, 78.221, 82.942, 85.380, 86.072},
                                {50.899, 55.641, 60.699, 66.002, 71.522, 77.128, 82.029, 84.597, 85.316},
                                {49.627, 54.353, 59.421, 64.768, 70.367, 76.093, 81.168, 83.871, 84.621},
                                {48.461, 53.165, 58.232, 63.609, 69.273, 75.106, 80.350, 83.195, 83.977},
                                {47.388, 52.062, 57.120, 62.516, 68.232, 74.161, 79.568, 82.560, 83.378},
                                {46.396, 51.037, 56.077, 61.482, 67.238, 73.252, 78.817, 81.962, 82.818},
                                {45.476, 50.078, 55.096, 60.500, 66.286, 72.376, 78.093, 81.394, 82.291},
                                {44.620, 49.181, 54.169, 59.565, 65.373, 71.528, 77.391, 80.854, 81.795},
                                {43.824, 48.338, 53.292, 58.674, 64.494, 70.706, 76.708, 80.339, 81.325},
                                {43.080, 47.545, 52.461, 57.821, 63.646, 69.907, 76.042, 79.844, 80.879},
                                {40.004, 44.183, 48.853, 54.039, 59.802, 66.201, 72.907, 77.620, 78.941},
                                {37.729, 41.577, 45.946, 50.878, 56.472, 62.874, 69.995, 75.675, 77.358},
                                {36.004, 39.504, 43.545, 48.183, 53.544, 59.850, 67.239, 73.887, 76.019},
                                {34.667, 37.822, 41.534, 45.863, 50.957, 57.096, 64.619, 72.176, 74.859},
                                {33.609, 36.437, 39.833, 43.857, 48.669, 54.596, 62.136, 70.489, 73.836},
                                {32.751, 35.278, 38.381, 42.117, 46.649, 52.335, 59.801, 68.796, 72.921},
                                {32.037, 34.294, 37.133, 40.602, 44.866, 50.299, 57.618, 67.087, 72.093},
                                {31.426, 33.444, 36.047, 39.275, 43.290, 48.468, 55.592, 65.366, 71.337},
                                {30.888, 32.695, 35.092, 38.106, 41.892, 46.823, 53.718, 63.647, 70.642},
                                {30.399, 32.023, 34.240, 37.065, 40.643, 45.341, 51.990, 61.947, 69.998},
                                {29.944, 31.408, 33.470, 36.128, 39.521, 44.000, 50.396, 60.281, 69.399},
                                {29.509, 30.836, 32.762, 35.274, 38.503, 42.780, 48.925, 58.662, 68.838},
                                {29.085, 30.294, 32.103, 34.488, 37.570, 41.663, 47.565, 57.098, 68.312},
                                {28.666, 29.773, 31.482, 33.755, 36.706, 40.633, 46.302, 55.595, 67.815},
                                {28.247, 29.267, 30.888, 33.063, 35.899, 39.675, 45.125, 54.155, 67.346},
                                {27.826, 28.770, 30.315, 32.404, 35.138, 38.778, 44.023, 52.777, 66.900},
                                {26.967, 27.790, 29.209, 31.156, 33.719, 37.126, 42.005, 50.201, 66.072},
                                {26.083, 26.814, 28.135, 29.970, 32.396, 35.615, 40.184, 47.843, 65.316},
                                {25.173, 25.831, 27.073, 28.818, 31.137, 34.202, 38.509, 45.674, 64.621},
                                {24.238, 24.838, 26.015, 27.688, 29.918, 32.858, 36.945, 43.666, 63.977},
                                {23.282, 23.834, 24.955, 26.569, 28.727, 31.564, 35.467, 41.794, 63.378},
                                {22.307, 22.818, 23.893, 25.457, 27.556, 30.307, 34.055, 40.036, 62.818},
                                {21.316, 21.793, 22.828, 24.351, 26.401, 29.080, 32.697, 38.374, 62.291},
                                {20.312, 20.761, 21.761, 23.249, 25.258, 27.878, 31.383, 36.794, 61.795},
                                {19.299, 19.724, 20.695, 22.153, 24.127, 26.696, 30.106, 35.285, 61.325},
                                {18.279, 18.684, 19.630, 21.063, 23.008, 25.534, 28.861, 33.837, 60.879},
                                {15.719, 16.089, 16.987, 18.372, 20.260, 22.703, 25.871, 30.439, 59.856},
                                {13.173, 13.522, 14.386, 15.738, 17.586, 19.971, 23.026, 27.296, 58.941},
                                {10.667, 11.000, 11.841, 13.170, 14.990, 17.332, 20.304, 24.352, 58.113},
                                {8.211, 8.533, 9.358, 10.669, 12.469, 14.780, 17.690, 21.568, 57.358},
                                {5.809, 6.122, 6.935, 8.234, 10.017, 12.304, 15.167, 18.915, 56.662},
                                {3.457, 3.763, 4.567, 5.856, 7.627, 9.896, 12.722, 16.367, 56.019},
                                {1.150, 1.449, 2.247, 3.528, 5.290, 7.544, 10.341, 13.904, 55.419},
                                {-1.122, -0.827, -0.035, 1.240, 2.994, 5.237, 8.010, 11.508, 54.859},
                                {-3.366, -3.076, -2.288, -1.018, 0.730, 2.963, 5.718, 9.161, 54.332},
                                {-5.593, -5.306, -4.521, -3.255, -1.512, 0.713, 3.452, 6.851, 53.836},
                                {-7.810, -7.526, -6.744, -5.482, -3.743, -1.524, 1.202, 4.563, 53.366},
                                {-10.026, -9.744, -8.965, -7.705, -5.970, -3.757, -1.041, 2.289, 52.921},
                                {-12.248, -11.967, -11.190, -9.933, -8.201, -5.992, -3.286, 0.018, 52.497},
                                {-14.480, -14.202, -13.426, -12.171, -10.441, -8.236, -5.538, -2.257, 52.093},
                                {-16.728, -16.451, -15.677, -14.423, -12.695, -10.494, -7.803, -4.541, 51.707},
                                {-18.993, -18.717, -17.945, -16.693, -14.967, -12.768, -10.083, -6.838, 51.337},
                                {-21.278, -21.004, -20.232, -18.981, -17.257, -15.061, -12.381, -9.150, 50.982},
                                {-23.582, -23.309, -22.538, -21.288, -19.566, -17.372, -14.696, -11.479, 50.642},
                                {-25.904, -25.631, -24.861, -23.612, -21.891, -19.699, -17.027, -13.821, 50.314},
                                {-28.239, -27.967, -27.198, -25.950, -24.230, -22.040, -19.371, -16.175, 49.998},
                                {-30.584, -30.313, -29.545, -28.297, -26.578, -24.389, -21.724, -18.536, 49.693},
                                {-32.933, -32.662, -31.894, -30.648, -28.929, -26.742, -24.079, -20.899, 49.399},
                                {-35.277, -35.007, -34.240, -32.994, -31.276, -29.090, -26.430, -23.257, 49.114},
                                {-37.610, -37.340, -36.573, -35.328, -33.611, -31.426, -28.768, -25.602, 48.838},
                                {-39.922, -39.652, -38.886, -37.641, -35.925, -33.740, -31.084, -27.924, 48.571},
                                {-42.203, -41.933, -41.168, -39.923, -38.207, -36.024, -33.370, -30.215, 48.312},
                                {-44.443, -44.174, -43.409, -42.165, -40.450, -38.267, -35.614, -32.464, 48.060},
                                {-46.633, -46.365, -45.600, -44.356, -42.641, -40.459, -37.808, -34.662, 47.815},
                                {-48.763, -48.494, -47.730, -46.486, -44.772, -42.591, -39.941, -36.798, 47.577},
                                {-50.822, -50.554, -49.790, -48.547, -46.833, -44.652, -42.003, -38.864, 47.346},
                                {-52.803, -52.535, -51.771, -50.528, -48.815, -46.635, -43.987, -40.851, 47.120},
                                {-54.698, -54.430, -53.666, -52.424, -50.710, -48.531, -45.884, -42.751, 46.900},
                        },
                        {// 600 MHz 1%
                                {92.788, 94.892, 97.076, 99.699, 102.345, 104.591, 106.007, 106.629, 106.900},
                                {82.390, 85.130, 87.816, 91.033, 94.401, 97.503, 99.622, 100.542, 100.879},
                                {76.031, 79.199, 82.230, 85.816, 89.602, 93.221, 95.829, 96.974, 97.358},
                                {71.287, 74.801, 78.119, 82.004, 86.105, 90.104, 93.101, 94.437, 94.859},
                                {67.459, 71.245, 74.808, 78.950, 83.315, 87.625, 90.956, 92.465, 92.921},
                                {64.233, 68.233, 72.001, 76.367, 80.963, 85.543, 89.173, 90.848, 91.337},
                                {61.442, 65.608, 69.545, 74.107, 78.907, 83.727, 87.637, 89.475, 89.998},
                                {58.981, 63.277, 67.353, 72.083, 77.064, 82.101, 86.274, 88.279, 88.838},
                                {56.781, 61.178, 65.368, 70.241, 75.382, 80.614, 85.040, 87.217, 87.815},
                                {54.794, 59.270, 63.553, 68.547, 73.827, 79.235, 83.902, 86.259, 86.900},
                                {52.982, 57.520, 61.879, 66.974, 72.376, 77.942, 82.838, 85.382, 86.072},
                                {51.320, 55.907, 60.326, 65.506, 71.013, 76.718, 81.832, 84.572, 85.316},
                                {49.785, 54.410, 58.878, 64.129, 69.726, 75.555, 80.872, 83.816, 84.621},
                                {48.361, 53.016, 57.524, 62.833, 68.505, 74.443, 79.950, 83.103, 83.977},
                                {47.035, 51.712, 56.251, 61.608, 67.345, 73.377, 79.059, 82.428, 83.378},
                                {45.794, 50.488, 55.052, 60.449, 66.240, 72.354, 78.196, 81.783, 82.818},
                                {44.630, 49.336, 53.919, 59.348, 65.184, 71.369, 77.358, 81.193, 82.291},
                                {43.535, 48.249, 52.846, 58.300, 64.174, 70.420, 76.542, 80.637, 81.795},
                                {42.502, 47.220, 51.828, 57.302, 63.207, 69.506, 75.747, 80.104, 81.325},
                                {41.527, 46.246, 50.860, 56.349, 62.279, 68.623, 74.987, 79.591, 80.879},
                                {37.346, 42.033, 46.640, 52.150, 58.144, 64.629, 71.455, 77.239, 78.941},
                                {34.052, 38.661, 43.212, 48.682, 54.670, 61.210, 68.237, 75.108, 77.358},
                                {31.393, 35.890, 40.350, 45.739, 51.677, 58.225, 65.349, 73.200, 76.019},
                                {29.207, 33.564, 37.907, 43.185, 49.043, 55.568, 62.793, 71.296, 74.859},
                                {27.383, 31.579, 35.785, 40.927, 46.680, 53.158, 60.456, 69.318, 73.836},
                                {25.843, 29.860, 33.912, 38.900, 44.527, 50.940, 58.287, 67.213, 72.921},
                                {24.524, 28.352, 32.239, 37.057, 42.543, 48.871, 56.248, 64.966, 72.093},
                                {23.382, 27.014, 30.728, 35.366, 40.697, 46.924, 54.311, 62.835, 71.337},
                                {22.380, 25.814, 29.351, 33.803, 38.970, 45.081, 52.456, 61.203, 70.642},
                                {21.491, 24.729, 28.089, 32.352, 37.348, 43.329, 50.672, 59.607, 69.998},
                                {20.692, 23.738, 26.924, 30.999, 35.819, 41.661, 48.950, 58.041, 69.399},
                                {19.965, 22.826, 25.843, 29.733, 34.377, 40.071, 47.286, 56.497, 68.838},
                                {19.295, 21.981, 24.836, 28.546, 33.016, 38.554, 45.677, 54.975, 68.312},
                                {18.671, 21.192, 23.893, 27.430, 31.728, 37.108, 44.122, 53.471, 67.815},
                                {18.085, 20.450, 23.005, 26.378, 30.510, 35.730, 42.620, 51.988, 67.346},
                                {17.527, 19.748, 22.167, 25.384, 29.356, 34.416, 41.172, 50.527, 66.900},
                                {16.476, 18.440, 20.613, 23.546, 27.220, 31.969, 38.431, 47.675, 66.072},
                                {15.482, 17.228, 19.189, 21.876, 25.284, 29.739, 35.891, 44.932, 65.316},
                                {14.522, 16.084, 17.865, 20.338, 23.512, 27.699, 33.539, 42.310, 64.621},
                                {13.581, 14.987, 16.614, 18.906, 21.876, 25.820, 31.358, 39.815, 63.977},
                                {12.649, 13.922, 15.419, 17.555, 20.349, 24.076, 29.331, 37.449, 63.378},
                                {11.721, 12.880, 14.265, 16.269, 18.911, 22.447, 27.438, 35.210, 62.818},
                                {10.792, 11.853, 13.143, 15.033, 17.544, 20.911, 25.663, 33.091, 62.291},
                                {9.862, 10.837, 12.045, 13.837, 16.235, 19.455, 23.989, 31.086, 61.795},
                                {8.930, 9.828, 10.965, 12.674, 14.974, 18.065, 22.404, 29.185, 61.325},
                                {7.994, 8.825, 9.901, 11.538, 13.753, 16.731, 20.895, 27.380, 60.879},
                                {5.644, 6.338, 7.292, 8.788, 10.837, 13.589, 17.392, 23.230, 59.856},
                                {3.288, 3.878, 4.744, 6.139, 8.069, 10.656, 14.188, 19.503, 58.941},
                                {0.937, 1.448, 2.250, 3.572, 5.415, 7.880, 11.207, 16.105, 58.113},
                                {-1.396, -0.946, -0.192, 1.075, 2.853, 5.227, 8.396, 12.965, 57.358},
                                {-3.705, -3.301, -2.583, -1.357, 0.372, 2.676, 5.723, 10.029, 56.662},
                                {-5.985, -5.616, -4.926, -3.731, -2.040, 0.209, 3.160, 7.256, 56.019},
                                {-8.235, -7.892, -7.224, -6.054, -4.393, -2.187, 0.688, 4.614, 55.419},
                                {-10.456, -10.134, -9.483, -8.332, -6.695, -4.523, -1.709, 2.079, 54.859},
                                {-12.651, -12.345, -11.708, -10.572, -8.954, -6.810, -4.046, -0.372, 54.332},
                                {-14.825, -14.532, -13.905, -12.782, -11.180, -9.059, -6.335, -2.755, 53.836},
                                {-16.983, -16.700, -16.082, -14.970, -13.380, -11.278, -8.588, -5.086, 53.366},
                                {-19.130, -18.856, -18.245, -17.141, -15.562, -13.476, -10.814, -7.378, 52.921},
                                {-21.273, -21.005, -20.400, -19.304, -17.734, -15.660, -13.022, -9.642, 52.497},
                                {-23.415, -23.153, -22.553, -21.463, -19.900, -17.838, -15.220, -11.888, 52.093},
                                {-25.562, -25.304, -24.708, -23.623, -22.068, -20.015, -17.414, -14.122, 51.707},
                                {-27.716, -27.463, -26.871, -25.790, -24.240, -22.195, -19.610, -16.353, 51.337},
                                {-29.882, -29.632, -29.043, -27.966, -26.420, -24.383, -21.810, -18.584, 50.982},
                                {-32.059, -31.812, -31.226, -30.152, -28.611, -26.579, -24.018, -20.818, 50.642},
                                {-34.249, -34.004, -33.420, -32.350, -30.812, -28.786, -26.234, -23.057, 50.314},
                                {-36.450, -36.208, -35.626, -34.558, -33.023, -31.002, -28.459, -25.302, 49.998},
                                {-38.661, -38.420, -37.840, -36.774, -35.243, -33.225, -30.690, -27.551, 49.693},
                                {-40.877, -40.638, -40.059, -38.995, -37.466, -35.452, -32.924, -29.801, 49.399},
                                {-43.093, -42.856, -42.279, -41.217, -39.690, -37.679, -35.157, -32.049, 49.114},
                                {-45.305, -45.069, -44.493, -43.433, -41.908, -39.900, -37.383, -34.287, 48.838},
                                {-47.505, -47.270, -46.695, -45.636, -44.113, -42.108, -39.595, -36.511, 48.571},
                                {-49.684, -49.450, -48.877, -47.819, -46.297, -44.294, -41.786, -38.712, 48.312},
                                {-51.835, -51.602, -51.029, -49.973, -48.452, -46.452, -43.947, -40.883, 48.060},
                                {-53.948, -53.716, -53.144, -52.089, -50.570, -48.571, -46.070, -43.014, 47.815},
                                {-56.015, -55.784, -55.213, -54.158, -52.640, -50.643, -48.145, -45.097, 47.577},
                                {-58.026, -57.795, -57.225, -56.171, -54.654, -52.658, -50.163, -47.122, 47.346},
                                {-59.971, -59.742, -59.172, -58.119, -56.603, -54.609, -52.116, -49.081, 47.120},
                                {-61.844, -61.615, -61.045, -59.993, -58.478, -56.485, -53.995, -50.966, 46.900},
                        },
                        {// 2000 MHz 1%
                                {94.233, 96.509, 98.662, 101.148, 103.509, 105.319, 106.328, 106.732, 106.900},
                                {82.711, 86.063, 88.943, 92.187, 95.445, 98.251, 99.972, 100.647, 100.879},
                                {75.466, 79.573, 82.996, 86.732, 90.502, 93.925, 96.182, 97.077, 97.358},
                                {70.027, 74.676, 78.565, 82.726, 86.888, 90.763, 93.451, 94.539, 94.859},
                                {65.657, 70.683, 74.957, 79.501, 84.003, 88.248, 91.304, 92.565, 92.921},
                                {62.006, 67.294, 71.875, 76.760, 81.573, 86.141, 89.526, 90.949, 91.337},
                                {58.874, 64.345, 69.167, 74.349, 79.451, 84.314, 88.001, 89.580, 89.998},
                                {56.136, 61.735, 66.743, 72.180, 77.547, 82.686, 86.660, 88.391, 88.838},
                                {53.705, 59.397, 64.548, 70.199, 75.807, 81.206, 85.457, 87.339, 87.815},
                                {51.522, 57.280, 62.541, 68.369, 74.194, 79.839, 84.359, 86.395, 86.900},
                                {49.543, 55.348, 60.693, 66.667, 72.684, 78.558, 83.344, 85.538, 86.072},
                                {47.736, 53.573, 58.982, 65.075, 71.260, 77.348, 82.394, 84.751, 85.316},
                                {46.073, 51.933, 57.390, 63.580, 69.909, 76.194, 81.495, 84.023, 84.621},
                                {44.536, 50.409, 55.903, 62.170, 68.623, 75.088, 80.638, 83.345, 83.977},
                                {43.109, 48.989, 54.508, 60.836, 67.396, 74.022, 79.815, 82.708, 83.378},
                                {41.777, 47.658, 53.196, 59.572, 66.220, 72.993, 79.018, 82.107, 82.818},
                                {40.531, 46.409, 51.957, 58.371, 65.094, 71.996, 78.243, 81.536, 82.291},
                                {39.362, 45.232, 50.786, 57.227, 64.011, 71.028, 77.487, 80.991, 81.795},
                                {38.261, 44.120, 49.675, 56.136, 62.971, 70.088, 76.745, 80.469, 81.325},
                                {37.223, 43.068, 48.619, 55.093, 61.969, 69.173, 76.017, 79.966, 80.879},
                                {32.791, 38.529, 44.017, 50.483, 57.457, 64.947, 72.530, 77.659, 78.941},
                                {29.313, 34.900, 40.271, 46.649, 53.606, 61.212, 69.260, 75.604, 77.358},
                                {26.509, 31.910, 37.130, 43.372, 50.246, 57.871, 66.197, 73.633, 76.019},
                                {24.203, 29.394, 34.437, 40.508, 47.258, 54.843, 63.329, 71.641, 74.859},
                                {22.275, 27.239, 32.087, 37.965, 44.561, 52.066, 60.636, 69.571, 73.836},
                                {20.641, 25.367, 30.009, 35.677, 42.098, 49.494, 58.096, 67.447, 72.921},
                                {19.239, 23.722, 28.150, 33.599, 39.830, 47.095, 55.688, 65.425, 72.093},
                                {18.021, 22.260, 26.473, 31.696, 37.727, 44.844, 53.398, 63.427, 71.337},
                                {16.951, 20.949, 24.948, 29.945, 35.771, 42.726, 51.214, 61.454, 70.642},
                                {16.000, 19.764, 23.554, 28.327, 33.944, 40.727, 49.126, 59.511, 69.998},
                                {15.145, 18.684, 22.271, 26.825, 32.235, 38.839, 47.129, 57.605, 69.399},
                                {14.368, 17.692, 21.086, 25.427, 30.632, 37.054, 45.218, 55.738, 68.838},
                                {13.655, 16.776, 19.985, 24.122, 29.128, 35.364, 43.389, 53.914, 68.312},
                                {12.994, 15.924, 18.957, 22.901, 27.713, 33.764, 41.639, 52.135, 67.815},
                                {12.375, 15.126, 17.995, 21.754, 26.380, 32.249, 39.964, 50.402, 67.346},
                                {11.791, 14.375, 17.089, 20.675, 25.123, 30.812, 38.362, 48.717, 66.900},
                                {10.700, 12.985, 15.421, 18.690, 22.808, 28.153, 35.364, 45.491, 66.072},
                                {9.683, 11.711, 13.906, 16.898, 20.722, 25.747, 32.617, 42.457, 65.316},
                                {8.715, 10.523, 12.510, 15.262, 18.825, 23.557, 30.096, 39.612, 64.621},
                                {7.777, 9.397, 11.205, 13.748, 17.082, 21.550, 27.774, 36.945, 63.977},
                                {6.859, 8.317, 9.971, 12.333, 15.466, 19.698, 25.627, 34.447, 63.378},
                                {5.951, 7.270, 8.792, 10.997, 13.955, 17.974, 23.632, 32.104, 62.818},
                                {5.051, 6.249, 7.656, 9.725, 12.529, 16.360, 21.768, 29.903, 62.291},
                                {4.153, 5.247, 6.554, 8.505, 11.173, 14.837, 20.017, 27.831, 61.795},
                                {3.257, 4.260, 5.480, 7.328, 9.877, 13.392, 18.366, 25.876, 61.325},
                                {2.362, 3.284, 4.428, 6.186, 8.631, 12.013, 16.800, 24.025, 60.879},
                                {0.125, 0.885, 1.876, 3.452, 5.686, 8.798, 13.191, 19.788, 59.856},
                                {-2.108, -1.470, -0.591, 0.851, 2.928, 5.835, 9.922, 16.003, 58.941},
                                {-4.330, -3.785, -2.991, -1.650, 0.309, 3.058, 6.905, 12.567, 58.113},
                                {-6.534, -6.061, -5.332, -4.069, -2.202, 0.425, 4.084, 9.403, 57.358},
                                {-8.715, -8.299, -7.621, -6.417, -4.622, -2.092, 1.415, 6.456, 56.662},
                                {-10.870, -10.499, -9.861, -8.705, -6.967, -4.514, -1.129, 3.683, 56.019},
                                {-12.999, -12.663, -12.058, -10.941, -9.248, -6.858, -3.572, 1.050, 55.419},
                                {-15.104, -14.797, -14.217, -13.131, -11.476, -9.137, -5.933, -1.470, 54.859},
                                {-17.187, -16.903, -16.345, -15.285, -13.661, -11.364, -8.229, -3.897, 54.332},
                                {-19.253, -18.989, -18.449, -17.410, -15.811, -13.549, -10.471, -6.252, 53.836},
                                {-21.307, -21.060, -20.534, -19.513, -17.936, -15.704, -12.675, -8.551, 53.366},
                                {-23.356, -23.122, -22.609, -21.603, -20.045, -17.838, -14.850, -10.807, 52.921},
                                {-25.405, -25.183, -24.680, -23.687, -22.144, -19.959, -17.006, -13.034, 52.497},
                                {-27.461, -27.248, -26.754, -25.772, -24.243, -22.077, -19.154, -15.243, 52.093},
                                {-29.527, -29.323, -28.837, -27.865, -26.347, -24.197, -21.301, -17.443, 51.707},
                                {-31.610, -31.413, -30.934, -29.970, -28.463, -26.327, -23.454, -19.642, 51.337},
                                {-33.714, -33.523, -33.050, -32.093, -30.595, -28.471, -25.618, -21.848, 50.982},
                                {-35.840, -35.654, -35.187, -34.237, -32.746, -30.633, -27.798, -24.064, 50.642},
                                {-37.990, -37.810, -37.347, -36.402, -34.919, -32.815, -29.996, -26.294, 50.314},
                                {-40.165, -39.989, -39.530, -38.591, -37.113, -35.018, -32.213, -28.540, 49.998},
                                {-42.363, -42.190, -41.735, -40.800, -39.328, -37.241, -34.449, -30.801, 49.693},
                                {-44.579, -44.410, -43.959, -43.027, -41.560, -39.480, -36.699, -33.074, 49.399},
                                {-46.810, -46.644, -46.195, -45.268, -43.805, -41.731, -38.960, -35.356, 49.114},
                                {-49.047, -48.884, -48.438, -47.514, -46.055, -43.986, -41.225, -37.639, 48.838},
                                {-51.283, -51.122, -50.679, -49.758, -48.302, -46.239, -43.485, -39.917, 48.571},
                                {-53.507, -53.349, -52.907, -51.989, -50.537, -48.478, -45.732, -42.179, 48.312},
                                {-55.708, -55.552, -55.113, -54.196, -52.748, -50.693, -47.954, -44.414, 48.060},
                                {-57.874, -57.720, -57.282, -56.368, -54.922, -52.871, -50.138, -46.612, 47.815},
                                {-59.992, -59.839, -59.403, -58.491, -57.048, -55.000, -52.273, -48.758, 47.577},
                                {-62.049, -61.898, -61.463, -60.553, -59.112, -57.067, -54.345, -50.841, 47.346},
                                {-64.033, -63.883, -63.450, -62.541, -61.102, -59.060, -56.343, -52.849, 47.120},
                                {-65.931, -65.783, -65.351, -64.444, -63.007, -60.968, -58.255, -54.770, 46.900},
                        },
                },
                {
                        {// 100 MHz 10%
                                {89.976, 92.181, 94.636, 97.385, 100.318, 103.121, 105.243, 106.357, 106.900},
                                {80.275, 83.091, 86.001, 89.208, 92.674, 96.120, 98.858, 100.285, 100.879},
                                {74.166, 77.530, 80.823, 84.350, 88.143, 91.969, 95.096, 96.731, 97.358},
                                {69.518, 73.355, 77.015, 80.831, 84.885, 88.993, 92.412, 94.208, 94.859},
                                {65.699, 69.921, 73.925, 78.021, 82.314, 86.660, 90.320, 92.250, 92.921},
                                {62.436, 66.958, 71.272, 75.641, 80.164, 84.727, 88.600, 90.649, 91.337},
                                {59.580, 64.332, 68.916, 73.542, 78.292, 83.063, 87.135, 89.294, 89.998},
                                {57.041, 61.967, 66.778, 71.642, 76.613, 81.589, 85.853, 88.119, 88.838},
                                {54.756, 59.814, 64.813, 69.890, 75.073, 80.252, 84.707, 87.080, 87.815},
                                {52.680, 57.838, 62.990, 68.255, 73.638, 79.018, 83.666, 86.148, 86.900},
                                {50.778, 56.013, 61.289, 66.716, 72.284, 77.859, 82.704, 85.301, 86.072},
                                {49.180, 54.364, 59.746, 65.286, 70.996, 76.760, 81.805, 84.525, 85.316},
                                {47.759, 52.947, 58.367, 63.988, 69.789, 75.706, 80.954, 83.807, 84.621},
                                {46.447, 51.630, 57.074, 62.759, 68.664, 74.690, 80.141, 83.136, 83.977},
                                {45.231, 50.401, 55.857, 61.591, 67.584, 73.727, 79.358, 82.506, 83.378},
                                {44.100, 49.251, 54.708, 60.477, 66.544, 72.807, 78.597, 81.910, 82.818},
                                {43.044, 48.170, 53.621, 59.412, 65.541, 71.912, 77.861, 81.343, 82.291},
                                {42.057, 47.152, 52.589, 58.393, 64.569, 71.038, 77.159, 80.801, 81.795},
                                {41.130, 46.191, 51.608, 57.415, 63.628, 70.183, 76.469, 80.289, 81.325},
                                {40.259, 45.283, 50.673, 56.475, 62.715, 69.345, 75.791, 79.796, 80.879},
                                {36.594, 41.383, 46.584, 52.272, 58.520, 65.373, 72.496, 77.546, 78.941},
                                {33.803, 38.310, 43.255, 48.733, 54.852, 61.735, 69.304, 75.494, 77.358},
                                {31.635, 35.836, 40.493, 45.712, 51.624, 58.418, 66.224, 73.502, 76.019},
                                {29.923, 33.812, 38.169, 43.107, 48.774, 55.405, 63.332, 71.501, 74.859},
                                {28.551, 32.133, 36.192, 40.844, 46.248, 52.676, 60.747, 69.469, 73.836},
                                {27.432, 30.721, 34.494, 38.865, 44.002, 50.206, 58.301, 67.414, 72.921},
                                {26.500, 29.516, 33.018, 37.122, 41.999, 47.967, 55.958, 65.360, 72.093},
                                {25.707, 28.470, 31.722, 35.575, 40.203, 45.934, 53.684, 63.382, 71.337},
                                {25.015, 27.547, 30.570, 34.191, 38.584, 44.083, 51.458, 61.540, 70.642},
                                {24.394, 26.718, 29.533, 32.941, 37.115, 42.390, 49.441, 59.705, 69.998},
                                {23.823, 25.960, 28.587, 31.800, 35.773, 40.835, 47.670, 57.866, 69.399},
                                {23.285, 25.255, 27.712, 30.749, 34.537, 39.398, 46.017, 56.017, 68.838},
                                {22.768, 24.588, 26.892, 29.771, 33.389, 38.062, 44.469, 54.155, 68.312},
                                {22.263, 23.949, 26.116, 28.851, 32.314, 36.812, 43.014, 52.391, 67.815},
                                {21.762, 23.329, 25.373, 27.978, 31.300, 35.637, 41.641, 50.805, 67.346},
                                {21.262, 22.721, 24.655, 27.142, 30.337, 34.524, 40.340, 49.282, 66.900},
                                {20.249, 21.527, 23.270, 25.555, 28.527, 32.450, 37.919, 46.410, 66.072},
                                {19.211, 20.340, 21.927, 24.046, 26.834, 30.533, 35.696, 43.745, 65.316},
                                {18.142, 19.149, 20.605, 22.587, 25.222, 28.732, 33.627, 41.260, 64.621},
                                {17.044, 17.947, 19.295, 21.162, 23.668, 27.019, 31.680, 38.929, 63.977},
                                {15.919, 16.734, 17.991, 19.762, 22.160, 25.375, 29.834, 36.732, 63.378},
                                {14.773, 15.513, 16.691, 18.381, 20.689, 23.788, 28.070, 34.653, 62.818},
                                {13.611, 14.286, 15.398, 17.019, 19.249, 22.249, 26.378, 32.677, 62.291},
                                {12.437, 13.057, 14.112, 15.674, 17.838, 20.752, 24.748, 30.792, 61.795},
                                {11.259, 11.831, 12.836, 14.347, 16.455, 19.295, 23.174, 28.991, 61.325},
                                {10.079, 10.609, 11.572, 13.040, 15.098, 17.874, 21.651, 27.264, 60.879},
                                {7.150, 7.599, 8.478, 9.861, 11.822, 14.470, 18.041, 23.232, 59.856},
                                {4.285, 4.676, 5.496, 6.817, 8.708, 11.263, 14.680, 19.550, 58.941},
                                {1.511, 1.861, 2.637, 3.912, 5.751, 8.236, 11.537, 16.156, 58.113},
                                {-1.162, -0.844, -0.101, 1.140, 2.939, 5.370, 8.581, 13.004, 57.358},
                                {-3.736, -3.442, -2.724, -1.510, 0.258, 2.647, 5.787, 10.054, 56.662},
                                {-6.218, -5.944, -5.246, -4.053, -2.310, 0.046, 3.130, 7.271, 56.019},
                                {-8.622, -8.363, -7.681, -6.505, -4.782, -2.452, 0.586, 4.624, 55.419},
                                {-10.961, -10.714, -10.045, -8.882, -7.175, -4.868, -1.867, 2.086, 54.859},
                                {-13.249, -13.012, -12.353, -11.202, -9.509, -7.220, -4.250, -0.367, 54.332},
                                {-15.500, -15.271, -14.622, -13.480, -11.798, -9.524, -6.580, -2.757, 53.836},
                                {-17.726, -17.505, -16.863, -15.729, -14.056, -11.795, -8.873, -5.101, 53.366},
                                {-19.941, -19.726, -19.090, -17.963, -16.298, -14.048, -11.144, -7.415, 52.921},
                                {-22.152, -21.942, -21.312, -20.191, -18.533, -16.292, -13.404, -9.712, 52.497},
                                {-24.369, -24.164, -23.538, -22.422, -20.769, -18.537, -15.663, -12.003, 52.093},
                                {-26.597, -26.396, -25.774, -24.662, -23.015, -20.789, -17.927, -14.295, 51.707},
                                {-28.840, -28.642, -28.024, -26.916, -25.273, -23.053, -20.202, -16.595, 51.337},
                                {-31.100, -30.905, -30.290, -29.185, -27.546, -25.332, -22.490, -18.904, 50.982},
                                {-33.377, -33.185, -32.572, -31.471, -29.835, -27.626, -24.792, -21.225, 50.642},
                                {-35.669, -35.479, -34.869, -33.770, -32.137, -29.932, -27.106, -23.555, 50.314},
                                {-37.971, -37.783, -37.175, -36.078, -34.448, -32.247, -29.427, -25.892, 49.998},
                                {-40.277, -40.091, -39.485, -38.390, -36.763, -34.565, -31.751, -28.229, 49.693},
                                {-42.580, -42.395, -41.791, -40.698, -39.073, -36.878, -34.069, -30.560, 49.399},
                                {-44.870, -44.687, -44.084, -42.993, -41.370, -39.178, -36.374, -32.875, 49.114},
                                {-47.137, -46.956, -46.355, -45.265, -43.644, -41.454, -38.654, -35.166, 48.838},
                                {-49.371, -49.191, -48.591, -47.503, -45.883, -43.695, -40.899, -37.420, 48.571},
                                {-51.560, -51.380, -50.782, -49.695, -48.077, -45.891, -43.098, -39.628, 48.312},
                                {-53.691, -53.513, -52.915, -51.829, -50.212, -48.029, -45.239, -41.776, 48.060},
                                {-55.754, -55.577, -54.980, -53.895, -52.280, -50.097, -47.311, -43.855, 47.815},
                                {-57.738, -57.561, -56.965, -55.882, -54.267, -52.087, -49.303, -45.853, 47.577},
                                {-59.632, -59.457, -58.862, -57.779, -56.165, -53.986, -51.205, -47.761, 47.346},
                                {-61.430, -61.255, -60.660, -59.578, -57.966, -55.788, -53.009, -49.570, 47.120},
                                {-63.123, -62.948, -62.355, -61.274, -59.662, -57.485, -54.709, -51.275, 46.900},
                        },
                        {// 600 MHz 10%
                                {92.788, 94.892, 97.076, 99.699, 102.345, 104.591, 106.007, 106.629, 106.900},
                                {81.956, 84.747, 87.449, 90.672, 94.076, 97.267, 99.511, 100.511, 100.879},
                                {74.848, 78.446, 81.617, 85.246, 89.076, 92.812, 95.623, 96.917, 97.358},
                                {69.340, 73.650, 77.292, 81.294, 85.451, 89.574, 92.819, 94.359, 94.859},
                                {64.860, 69.686, 73.762, 78.128, 82.577, 87.011, 90.613, 92.369, 92.921},
                                {61.111, 66.285, 70.727, 75.443, 80.171, 84.877, 88.786, 90.738, 91.337},
                                {57.905, 63.306, 68.041, 73.080, 78.076, 83.033, 87.219, 89.356, 89.998},
                                {55.112, 60.663, 65.622, 70.947, 76.202, 81.398, 85.842, 88.154, 88.838},
                                {52.644, 58.294, 63.421, 68.991, 74.491, 79.917, 84.607, 87.090, 87.815},
                                {50.438, 56.151, 61.403, 67.177, 72.904, 78.553, 83.481, 86.134, 86.900},
                                {48.448, 54.200, 59.543, 65.484, 71.416, 77.279, 82.440, 85.265, 86.072},
                                {46.638, 52.411, 57.819, 63.895, 70.010, 76.077, 81.467, 84.466, 85.316},
                                {44.982, 50.764, 56.216, 62.397, 68.673, 74.932, 80.549, 83.727, 84.621},
                                {43.459, 49.238, 54.719, 60.982, 67.395, 73.835, 79.675, 83.037, 83.977},
                                {42.051, 47.820, 53.316, 59.642, 66.172, 72.778, 78.835, 82.388, 83.378},
                                {40.746, 46.497, 51.998, 58.369, 64.997, 71.755, 78.025, 81.775, 82.818},
                                {39.531, 45.259, 50.757, 57.158, 63.866, 70.762, 77.239, 81.193, 82.291},
                                {38.398, 44.096, 49.583, 56.003, 62.777, 69.796, 76.473, 80.637, 81.795},
                                {37.338, 43.002, 48.472, 54.899, 61.725, 68.854, 75.723, 80.104, 81.325},
                                {36.344, 41.970, 47.417, 53.843, 60.708, 67.934, 74.987, 79.591, 80.879},
                                {32.186, 37.563, 42.829, 49.148, 56.071, 63.622, 71.455, 77.239, 78.941},
                                {29.036, 34.091, 39.096, 45.192, 52.015, 59.690, 68.237, 75.108, 77.358},
                                {26.584, 31.269, 35.962, 41.762, 48.386, 56.051, 65.125, 73.200, 76.019},
                                {24.632, 28.922, 33.274, 38.735, 45.095, 52.651, 61.999, 71.296, 74.859},
                                {23.045, 26.935, 30.938, 36.040, 42.094, 49.468, 58.862, 69.318, 73.836},
                                {21.725, 25.230, 28.891, 33.629, 39.356, 46.495, 55.739, 67.213, 72.921},
                                {20.605, 23.747, 27.083, 31.469, 36.864, 43.729, 52.782, 64.966, 72.093},
                                {19.631, 22.442, 25.478, 29.532, 34.600, 41.168, 50.059, 62.591, 71.337},
                                {18.766, 21.279, 24.043, 27.791, 32.545, 38.808, 47.476, 60.122, 70.642},
                                {17.980, 20.228, 22.749, 26.219, 30.680, 36.638, 45.042, 57.601, 69.998},
                                {17.252, 19.267, 21.572, 24.792, 28.984, 34.645, 42.757, 55.065, 69.399},
                                {16.564, 18.376, 20.490, 23.489, 27.434, 32.814, 40.618, 52.611, 68.838},
                                {15.905, 17.539, 19.487, 22.288, 26.012, 31.127, 38.619, 50.394, 68.312},
                                {15.265, 16.745, 18.546, 21.174, 24.698, 29.567, 36.751, 48.253, 67.815},
                                {14.638, 15.982, 17.656, 20.130, 23.476, 28.120, 35.005, 46.196, 67.346},
                                {14.017, 15.244, 16.806, 19.145, 22.332, 26.771, 33.370, 44.226, 66.900},
                                {12.784, 13.818, 15.196, 17.311, 20.231, 24.313, 30.390, 40.542, 66.072},
                                {11.547, 12.434, 13.669, 15.609, 18.317, 22.107, 27.731, 37.185, 65.316},
                                {10.300, 11.070, 12.194, 13.997, 16.535, 20.088, 25.326, 34.123, 64.621},
                                {9.039, 9.719, 10.754, 12.448, 14.851, 18.210, 23.122, 31.320, 63.977},
                                {7.768, 8.374, 9.339, 10.945, 13.239, 16.440, 21.077, 28.741, 63.378},
                                {6.488, 7.036, 7.943, 9.478, 11.683, 14.753, 19.161, 26.352, 62.818},
                                {5.204, 5.704, 6.564, 8.041, 10.173, 13.136, 17.349, 24.127, 62.291},
                                {3.919, 4.380, 5.201, 6.631, 8.703, 11.575, 15.624, 22.042, 61.795},
                                {2.638, 3.066, 3.855, 5.246, 7.267, 10.064, 13.974, 20.078, 61.325},
                                {1.364, 1.764, 2.527, 3.884, 5.863, 8.597, 12.388, 18.218, 60.879},
                                {-1.774, -1.424, -0.712, 0.583, 2.483, 5.096, 8.660, 13.949, 59.856},
                                {-4.816, -4.500, -3.822, -2.569, -0.723, 1.806, 5.211, 10.111, 58.941},
                                {-7.743, -7.452, -6.797, -5.575, -3.767, -1.296, 1.995, 6.610, 58.113},
                                {-10.550, -10.276, -9.638, -8.437, -6.657, -4.230, -1.021, 3.380, 57.358},
                                {-13.237, -12.976, -12.351, -11.166, -9.407, -7.011, -3.865, 0.375, 56.662},
                                {-15.812, -15.561, -14.945, -13.772, -12.029, -9.657, -6.560, -2.444, 56.019},
                                {-18.284, -18.041, -17.433, -16.269, -14.538, -12.186, -9.125, -5.108, 55.419},
                                {-20.668, -20.431, -19.828, -18.672, -16.950, -14.613, -11.582, -7.642, 54.859},
                                {-22.976, -22.743, -22.146, -20.995, -19.281, -16.956, -13.949, -10.072, 54.332},
                                {-25.222, -24.993, -24.400, -23.254, -21.546, -19.231, -16.243, -12.416, 53.836},
                                {-27.420, -27.194, -26.604, -25.462, -23.759, -21.452, -18.479, -14.695, 53.366},
                                {-29.583, -29.360, -28.772, -27.633, -25.935, -23.634, -20.675, -16.925, 52.921},
                                {-31.723, -31.502, -30.916, -29.780, -28.085, -25.790, -22.841, -19.121, 52.497},
                                {-33.851, -33.632, -33.048, -31.914, -30.222, -27.931, -24.992, -21.296, 52.093},
                                {-35.977, -35.760, -35.177, -34.045, -32.356, -30.069, -27.138, -23.463, 51.707},
                                {-38.110, -37.894, -37.313, -36.183, -34.495, -32.212, -29.287, -25.630, 51.337},
                                {-40.256, -40.041, -39.461, -38.333, -36.647, -34.367, -31.448, -27.806, 50.982},
                                {-42.421, -42.207, -41.628, -40.501, -38.817, -36.539, -33.625, -29.997, 50.642},
                                {-44.608, -44.395, -43.817, -42.691, -41.008, -38.733, -35.823, -32.207, 50.314},
                                {-46.819, -46.607, -46.029, -44.904, -43.223, -40.949, -38.043, -34.437, 49.998},
                                {-49.052, -48.840, -48.263, -47.139, -45.459, -43.186, -40.284, -36.687, 49.693},
                                {-51.305, -51.094, -50.517, -49.394, -47.714, -45.444, -42.544, -38.955, 49.399},
                                {-53.572, -53.362, -52.786, -51.663, -49.985, -47.715, -44.818, -41.236, 49.114},
                                {-55.848, -55.638, -55.063, -53.940, -52.262, -49.994, -47.100, -43.524, 48.838},
                                {-58.123, -57.913, -57.338, -56.216, -54.539, -52.272, -49.380, -45.810, 48.571},
                                {-60.386, -60.177, -59.603, -58.481, -56.805, -54.538, -51.648, -48.083, 48.312},
                                {-62.627, -62.418, -61.844, -60.723, -59.047, -56.781, -53.893, -50.332, 48.060},
                                {-64.832, -64.623, -64.049, -62.928, -61.253, -58.988, -56.101, -52.545, 47.815},
                                {-66.987, -66.779, -66.205, -65.085, -63.410, -61.146, -58.260, -54.707, 47.577},
                                {-69.080, -68.872, -68.298, -67.178, -65.504, -63.240, -60.356, -56.806, 47.346},
                                {-71.097, -70.889, -70.316, -69.196, -67.522, -65.259, -62.375, -58.829, 47.120},
                                {-73.026, -72.818, -72.245, -71.125, -69.452, -67.189, -64.307, -60.763, 46.900},
                        },
                        {// 2000 MHz 10%
                                {94.233, 96.509, 98.662, 101.148, 103.509, 105.319, 106.328, 106.732, 106.900},
                                {82.427, 85.910, 88.762, 92.000, 95.276, 98.138, 99.926, 100.635, 100.879},
                                {74.501, 79.135, 82.671, 86.423, 90.219, 93.718, 96.089, 97.054, 97.358},
                                {68.368, 73.847, 78.078, 82.310, 86.522, 90.481, 93.317, 94.505, 94.859},
                                {63.440, 69.412, 74.253, 79.006, 83.569, 87.906, 91.132, 92.522, 92.921},
                                {59.326, 65.580, 70.908, 76.172, 81.072, 85.751, 89.321, 90.898, 91.337},
                                {55.804, 62.216, 67.909, 73.643, 78.912, 83.880, 87.767, 89.521, 89.998},
                                {52.734, 59.227, 65.186, 71.329, 76.970, 82.211, 86.400, 88.324, 88.838},
                                {50.019, 56.544, 62.696, 69.181, 75.181, 80.707, 85.173, 87.266, 87.815},
                                {47.590, 54.149, 60.406, 67.169, 73.507, 79.331, 84.052, 86.316, 86.900},
                                {45.398, 51.989, 58.291, 65.277, 71.922, 78.043, 83.015, 85.452, 86.072},
                                {43.403, 50.015, 56.329, 63.490, 70.408, 76.822, 82.061, 84.660, 85.316},
                                {41.577, 48.200, 54.502, 61.800, 68.956, 75.653, 81.165, 83.927, 84.621},
                                {39.897, 46.523, 52.816, 60.198, 67.558, 74.524, 80.313, 83.244, 83.977},
                                {38.345, 44.967, 51.276, 58.677, 66.210, 73.429, 79.497, 82.608, 83.378},
                                {36.904, 43.519, 49.836, 57.231, 64.909, 72.361, 78.708, 82.009, 82.818},
                                {35.564, 42.166, 48.484, 55.853, 63.652, 71.316, 77.942, 81.442, 82.291},
                                {34.312, 40.898, 47.212, 54.557, 62.437, 70.293, 77.192, 80.903, 81.795},
                                {33.142, 39.706, 46.011, 53.367, 61.260, 69.289, 76.455, 80.387, 81.325},
                                {32.044, 38.584, 44.874, 52.235, 60.121, 68.303, 75.728, 79.893, 80.879},
                                {27.448, 33.810, 39.972, 47.266, 55.121, 63.628, 72.179, 77.642, 78.941},
                                {23.966, 30.064, 36.014, 43.134, 50.933, 59.475, 68.706, 75.604, 77.358},
                                {21.259, 27.018, 32.683, 39.544, 47.191, 55.784, 65.291, 73.633, 76.019},
                                {19.114, 24.470, 29.794, 36.325, 43.743, 52.298, 62.125, 71.641, 74.859},
                                {17.385, 22.298, 27.238, 33.389, 40.514, 48.952, 59.021, 69.571, 73.836},
                                {15.967, 20.421, 24.957, 30.694, 37.476, 45.725, 55.936, 67.447, 72.921},
                                {14.787, 18.784, 22.916, 28.226, 34.630, 42.624, 52.874, 65.223, 72.093},
                                {13.788, 17.350, 21.090, 25.978, 31.988, 39.673, 49.858, 62.869, 71.337},
                                {12.927, 16.087, 19.461, 23.944, 29.558, 36.896, 46.919, 60.403, 70.642},
                                {12.171, 14.968, 18.007, 22.113, 27.343, 34.310, 44.088, 57.862, 69.998},
                                {11.495, 13.969, 16.708, 20.471, 25.337, 31.926, 41.391, 55.285, 69.399},
                                {10.878, 13.069, 15.543, 18.997, 23.527, 29.743, 38.847, 52.709, 68.838},
                                {10.305, 12.251, 14.493, 17.673, 21.895, 27.753, 36.467, 50.169, 68.312},
                                {9.764, 11.497, 13.537, 16.476, 20.422, 25.942, 34.254, 47.690, 67.815},
                                {9.245, 10.796, 12.661, 15.389, 19.087, 24.294, 32.204, 45.293, 67.346},
                                {8.741, 10.134, 11.848, 14.392, 17.871, 22.792, 30.310, 42.991, 66.900},
                                {7.755, 8.897, 10.367, 12.611, 15.726, 20.153, 26.943, 38.707, 66.072},
                                {6.775, 7.731, 9.018, 11.035, 13.867, 17.896, 24.054, 34.861, 65.316},
                                {5.784, 6.599, 7.748, 9.591, 12.205, 15.917, 21.542, 31.431, 64.621},
                                {4.772, 5.480, 6.524, 8.234, 10.677, 14.137, 19.320, 28.371, 63.977},
                                {3.738, 4.362, 5.323, 6.930, 9.240, 12.499, 17.319, 25.629, 63.378},
                                {2.679, 3.238, 4.135, 5.660, 7.865, 10.963, 15.485, 23.151, 62.818},
                                {1.599, 2.104, 2.950, 4.410, 6.532, 9.500, 13.778, 20.890, 62.291},
                                {0.499, 0.961, 1.765, 3.174, 5.228, 8.092, 12.168, 18.808, 61.795},
                                {-0.618, -0.191, 0.580, 1.946, 3.946, 6.724, 10.634, 16.872, 61.325},
                                {-1.747, -1.350, -0.607, 0.726, 2.680, 5.388, 9.160, 15.056, 60.879},
                                {-4.605, -4.264, -3.571, -2.301, -0.427, 2.152, 5.669, 10.915, 59.856},
                                {-7.477, -7.174, -6.513, -5.283, -3.462, -0.966, 2.383, 7.187, 58.941},
                                {-10.327, -10.049, -9.410, -8.207, -6.421, -3.982, -0.747, 3.749, 58.113},
                                {-13.128, -12.868, -12.245, -11.060, -9.299, -6.899, -3.744, 0.533, 57.358},
                                {-15.865, -15.617, -15.005, -13.834, -12.090, -9.718, -6.621, -2.503, 56.662},
                                {-18.529, -18.290, -17.686, -16.524, -14.793, -12.442, -9.387, -5.387, 56.019},
                                {-21.116, -20.884, -20.286, -19.131, -17.410, -15.074, -12.051, -8.140, 55.419},
                                {-23.629, -23.402, -22.808, -21.659, -19.945, -17.621, -14.622, -10.779, 54.859},
                                {-26.072, -25.848, -25.257, -24.113, -22.405, -20.090, -17.110, -13.321, 54.332},
                                {-28.451, -28.230, -27.642, -26.501, -24.797, -22.490, -19.525, -15.778, 53.836},
                                {-30.775, -30.557, -29.971, -28.832, -27.132, -24.830, -21.878, -18.164, 53.366},
                                {-33.054, -32.837, -32.253, -31.116, -29.419, -27.122, -24.179, -20.493, 52.921},
                                {-35.296, -35.081, -34.498, -33.364, -31.669, -29.375, -26.440, -22.776, 52.497},
                                {-37.513, -37.299, -36.718, -35.584, -33.891, -31.601, -28.673, -25.027, 52.093},
                                {-39.714, -39.501, -38.920, -37.788, -36.097, -33.809, -30.886, -27.256, 51.707},
                                {-41.907, -41.695, -41.115, -39.984, -38.294, -36.009, -33.090, -29.473, 51.337},
                                {-44.102, -43.891, -43.312, -42.181, -40.492, -38.209, -35.294, -31.688, 50.982},
                                {-46.305, -46.095, -45.516, -44.386, -42.698, -40.416, -37.505, -33.907, 50.642},
                                {-48.522, -48.312, -47.733, -46.604, -44.917, -42.636, -39.728, -36.138, 50.314},
                                {-50.755, -50.546, -49.968, -48.839, -47.153, -44.873, -41.967, -38.384, 49.998},
                                {-53.007, -52.798, -52.220, -51.092, -49.406, -47.128, -44.223, -40.647, 49.693},
                                {-55.275, -55.066, -54.489, -53.361, -51.676, -49.399, -46.496, -42.924, 49.399},
                                {-57.557, -57.348, -56.771, -55.644, -53.959, -51.682, -48.781, -45.214, 49.114},
                                {-59.844, -59.636, -59.059, -57.932, -56.248, -53.971, -51.072, -47.508, 48.838},
                                {-62.128, -61.920, -61.343, -60.217, -58.532, -56.257, -53.358, -49.799, 48.571},
                                {-64.396, -64.188, -63.612, -62.485, -60.802, -58.527, -55.629, -52.073, 48.312},
                                {-66.635, -66.427, -65.851, -64.725, -63.041, -60.766, -57.870, -54.316, 48.060},
                                {-68.828, -68.620, -68.044, -66.918, -65.235, -62.960, -60.065, -56.513, 47.815},
                                {-70.958, -70.750, -70.174, -69.048, -67.365, -65.091, -62.197, -58.647, 47.577},
                                {-73.008, -72.801, -72.225, -71.099, -69.417, -67.143, -64.249, -60.701, 47.346},
                                {-74.964, -74.756, -74.180, -73.055, -71.372, -69.099, -66.205, -62.660, 47.120},
                                {-76.809, -76.602, -76.026, -74.901, -73.218, -70.945, -68.052, -64.508, 46.900},
                        },
                },
                {
                        {// 100 MHz 50%
                                {89.975852, 92.181157, 94.635547, 97.384505, 100.318067, 103.120502, 105.242609, 106.356584, 106.900},
                                {80.275128, 83.090807, 86.001380, 89.207574, 92.674184, 96.119692, 98.857707, 100.284591, 100.879},
                                {74.166239, 77.529572, 80.823450, 84.350391, 88.142741, 91.968629, 95.095801, 96.730553, 97.358},
                                {69.518418, 73.354816, 77.014863, 80.831220, 84.885018, 88.993402, 92.412470, 94.207699, 94.859},
                                {65.699420, 69.920619, 73.924833, 78.021358, 82.313726, 86.660124, 90.320287, 92.249770, 92.921},
                                {62.435852, 66.957760, 71.272338, 75.640654, 80.163501, 84.727135, 88.600472, 90.648882, 91.337},
                                {59.580317, 64.332176, 68.916118, 73.542303, 78.291523, 83.063318, 87.135157, 89.293970, 89.998},
                                {57.041171, 61.967283, 66.778309, 71.642351, 76.612716, 81.589073, 85.853051, 88.118573, 88.838},
                                {54.755982, 59.813965, 64.812656, 69.890311, 75.073307, 80.252365, 84.707392, 87.079662, 87.815},
                                {52.679639, 57.837704, 62.989553, 68.254774, 73.638200, 79.017504, 83.665614, 86.147696, 86.900},
                                {50.778224, 56.012601, 61.288551, 66.715560, 72.284151, 77.859317, 82.703995, 85.301451, 86.072},
                                {49.025504, 54.318341, 59.694488, 65.259188, 70.995694, 76.759834, 81.804680, 84.525108, 85.316},
                                {47.400748, 52.738521, 58.195448, 63.876181, 69.762520, 75.706242, 80.953922, 83.806508, 84.621},
                                {45.887292, 51.259635, 56.781648, 62.559472, 68.577707, 74.689515, 80.141009, 83.136070, 83.977},
                                {44.471549, 49.870406, 55.444826, 61.303469, 67.436528, 73.703427, 79.357567, 82.506076, 83.378},
                                {43.142302, 48.561319, 54.177862, 60.103501, 66.335633, 72.743795, 78.597090, 81.910200, 82.818},
                                {41.890198, 47.324273, 52.974551, 58.955517, 65.272514, 71.807899, 77.854603, 81.343177, 82.291},
                                {40.707358, 46.152327, 51.829432, 57.855898, 64.245156, 70.894024, 77.126392, 80.800578, 81.795},
                                {39.587089, 45.039488, 50.737681, 56.801363, 63.251832, 70.001117, 76.409783, 80.278643, 81.325},
                                {38.523662, 43.980553, 49.695009, 55.788902, 62.290968, 69.128528, 75.702949, 79.774160, 80.879},
                                {33.906877, 39.353167, 45.096999, 51.267648, 57.923133, 65.057416, 72.290194, 77.430187, 78.941},
                                {30.181101, 35.575326, 41.290077, 47.459335, 54.161053, 61.436134, 69.082123, 75.247050, 77.358},
                                {27.102165, 32.409272, 38.052688, 44.171197, 50.859376, 58.194286, 66.099112, 73.137574, 76.019},
                                {24.517820, 29.703944, 35.239021, 41.267845, 47.901729, 55.251096, 63.331892, 71.082292, 74.859},
                                {22.324232, 27.356109, 32.748142, 38.652583, 45.198990, 52.532544, 60.746502, 69.082851, 73.836},
                                {20.445670, 25.291689, 30.508235, 36.256256, 42.685292, 49.978308, 58.301307, 67.139023, 72.921},
                                {18.824162, 23.455968, 28.467890, 34.030371, 40.314208, 47.543206, 55.957510, 65.243052, 72.093},
                                {17.413824, 21.807891, 26.590669, 31.942327, 38.055323, 45.196392, 53.683883, 63.381760, 71.337},
                                {16.177532, 20.316355, 24.851189, 29.971576, 35.890970, 42.919531, 51.458278, 61.540308, 70.642},
                                {15.084811, 18.957532, 23.231999, 28.106245, 33.812984, 40.704406, 49.267439, 59.705222, 69.998},
                                {14.110428, 17.712841, 21.721003, 26.340144, 31.819589, 38.550265, 47.105869, 57.866225, 69.399},
                                {13.233379, 16.567400, 20.309404, 24.670258, 29.912603, 36.461160, 44.974219, 56.017025, 68.838},
                                {12.436137, 15.508884, 18.990163, 23.094821, 28.095169, 34.443562, 42.877491, 54.155361, 68.312},
                                {11.704060, 14.526731, 17.756983, 21.612009, 26.370112, 32.504418, 40.823265, 52.282561, 67.815},
                                {11.024921, 13.611629, 16.603707, 20.219207, 24.738938, 30.649757, 38.820156, 50.402856, 67.346},
                                {10.388525, 12.755210, 15.524069, 18.912717, 23.201374, 28.883852, 36.876559, 48.522573, 66.900},
                                {9.211460, 11.188785, 13.559993, 16.538857, 20.397099, 25.624759, 33.195430, 44.791371, 66.072},
                                {8.120958, 9.775007, 11.813599, 14.444151, 17.923485, 22.720205, 29.817323, 41.153416, 65.316},
                                {7.081758, 8.471760, 10.237193, 12.577955, 15.732972, 20.138921, 26.750860, 37.666046, 64.621},
                                {6.070394, 7.246432, 8.789752, 10.892439, 13.774902, 17.837208, 23.981533, 34.370263, 63.977},
                                {5.071714, 6.074722, 7.438162, 9.346499, 12.002404, 15.768665, 21.480733, 31.288462, 63.378},
                                {4.076377, 4.939190, 6.156958, 7.906945, 10.375587, 13.890122, 19.213831, 28.426623, 62.818},
                                {3.079057, 3.827778, 4.927268, 6.548108, 8.862259, 12.164385, 17.145787, 25.778524, 62.291},
                                {2.077166, 2.732491, 3.735540, 5.250735, 7.437347, 10.560916, 15.244355, 23.330171, 61.795},
                                {1.069944, 1.648310, 2.572312, 4.000724, 6.081793, 9.055424, 13.481508, 21.063518, 61.325},
                                {0.057808, 0.572330, 1.431171, 2.787940, 4.781360, 7.628982, 11.833768, 18.959170, 60.879},
                                {-2.485571, -2.088893, -1.348877, -0.122912, 1.709288, 4.321613, 8.100969, 14.287235, 59.856},
                                {-5.026417, -4.707595, -4.044637, -2.903540, -1.176759, 1.279258, 4.767244, 10.263072, 58.941},
                                {-7.538997, -7.273236, -6.661949, -5.577762, -3.922033, -1.572460, 1.713116, 6.706292, 58.113},
                                {-10.003377, -9.774718, -9.199125, -8.154263, -6.547809, -4.272572, -1.130267, 3.495549, 57.358},
                                {-12.406876, -12.204732, -11.654455, -10.637506, -9.066119, -6.844046, -3.805172, 0.549411, 56.662},
                                {-14.743468, -14.560645, -14.028746, -13.032086, -11.486241, -9.303027, -6.340266, -2.188532, 56.019},
                                {-17.012525, -16.844031, -16.325754, -15.344155, -13.817302, -11.663058, -8.757319, -4.759376, 55.419},
                                {-19.217485, -19.059792, -18.551801, -17.581593, -16.069123, -13.936864, -11.074556, -7.194799, 54.859},
                                {-21.364677, -21.215247, -20.715154, -19.753705, -18.252309, -16.137000, -13.308275, -9.520527, 54.332},
                                {-23.462348, -23.319328, -22.825390, -21.870779, -20.378035, -18.275985, -15.473584, -11.758346, 53.836},
                                {-25.519898, -25.381915, -24.892837, -23.943635, -22.457741, -20.366198, -17.584693, -13.927251, 53.366},
                                {-27.547270, -27.413291, -26.928099, -25.983228, -24.502821, -22.419702, -19.654974, -16.044088, 52.921},
                                {-29.554470, -29.423708, -28.941657, -28.000291, -26.524328, -24.448036, -21.696918, -18.123902, 52.497},
                                {-31.551176, -31.423023, -30.943536, -30.005036, -28.532708, -26.462001, -23.722031, -20.180098, 52.093},
                                {-33.546410, -33.420393, -32.943019, -32.006883, -30.537555, -28.471460, -25.740699, -22.224492, 51.707},
                                {-35.548264, -35.424010, -34.948391, -34.014221, -32.547389, -30.485133, -27.762042, -24.267293, 51.337},
                                {-37.563650, -37.440862, -36.966712, -36.034190, -34.569451, -32.510413, -29.793758, -26.317035, 50.982},
                                {-39.598079, -39.476519, -39.003609, -38.072478, -36.609505, -34.553186, -31.841967, -28.380489, 50.642},
                                {-41.655471, -41.534947, -41.063088, -40.133139, -38.671667, -36.617659, -33.911062, -30.462556, 50.314},
                                {-43.737982, -43.618337, -43.147376, -42.218437, -40.758248, -38.706215, -36.003571, -32.566167, 49.998},
                                {-45.845868, -45.726974, -45.256785, -44.328714, -42.869628, -40.819293, -38.120048, -34.692198, 49.693},
                                {-47.977393, -47.859144, -47.389621, -46.462300, -45.004167, -42.955299, -40.258993, -36.839404, 49.399},
                                {-50.128777, -50.011086, -49.542140, -48.615470, -47.158165, -45.110571, -42.416816, -39.004406, 49.114},
                                {-52.294211, -52.177004, -51.708563, -50.782459, -49.325876, -47.279393, -44.587865, -41.181720, 48.838},
                                {-54.465939, -54.349154, -53.881154, -52.955548, -51.499597, -49.454087, -46.764510, -43.363856, 48.571},
                                {-56.634413, -56.517998, -56.050386, -55.125217, -53.669822, -51.625169, -48.937307, -45.541484, 48.312},
                                {-58.788536, -58.672447, -58.205177, -57.280394, -55.825489, -53.781592, -51.095244, -47.703689, 48.060},
                                {-60.915978, -60.800177, -60.333211, -59.408770, -57.954300, -55.911072, -53.226066, -49.838292, 47.815},
                                {-63.003580, -62.888035, -62.421338, -61.497200, -60.043117, -58.000484, -55.316672, -51.932259, 47.577},
                                {-65.037809, -64.922493, -64.456036, -63.532169, -62.078430, -60.036329, -57.353580, -53.972166, 47.346},
                                {-67.005279, -66.890166, -66.423924, -65.500300, -64.046869, -62.005242, -59.323444, -55.944713, 47.120},
                                {-68.893276, -68.778346, -68.312296, -67.388890, -65.935735, -63.894534, -61.213590, -57.837266, 46.900},
                        },
                        {// 600 MHz 50%
                                {92.681, 94.868, 97.072, 99.699, 102.345, 104.591, 106.007, 106.629, 106.900},
                                {81.108, 84.291, 87.092, 90.356, 93.803, 97.071, 99.417, 100.484, 100.879},
                                {73.480, 77.690, 81.046, 84.741, 88.624, 92.462, 95.443, 96.866, 97.358},
                                {67.693, 72.675, 76.575, 80.667, 84.877, 89.107, 92.562, 94.285, 94.859},
                                {63.064, 68.556, 72.942, 77.421, 81.920, 86.457, 90.290, 92.275, 92.921},
                                {59.229, 65.047, 69.834, 74.687, 79.459, 84.256, 88.406, 90.626, 91.337},
                                {55.965, 61.992, 67.096, 72.296, 77.333, 82.365, 86.792, 89.227, 89.998},
                                {53.130, 59.293, 64.640, 70.152, 75.447, 80.700, 85.376, 88.010, 88.838},
                                {50.628, 56.879, 62.410, 68.195, 73.739, 79.204, 84.110, 86.933, 87.815},
                                {48.393, 54.701, 60.370, 66.387, 72.167, 77.839, 82.961, 85.965, 86.900},
                                {46.377, 52.719, 58.489, 64.702, 70.703, 76.576, 81.907, 85.085, 86.072},
                                {44.542, 50.904, 56.748, 63.122, 69.327, 75.396, 80.928, 84.279, 85.316},
                                {42.862, 49.230, 55.127, 61.633, 68.022, 74.282, 80.013, 83.533, 84.621},
                                {41.315, 47.680, 53.613, 60.224, 66.780, 73.223, 79.148, 82.838, 83.977},
                                {39.883, 46.238, 52.192, 58.888, 65.590, 72.209, 78.327, 82.187, 83.378},
                                {38.553, 44.890, 50.856, 57.617, 64.447, 71.233, 77.541, 81.574, 82.818},
                                {37.312, 43.626, 49.594, 56.404, 63.345, 70.289, 76.786, 80.993, 82.291},
                                {36.151, 42.437, 48.399, 55.244, 62.280, 69.373, 76.056, 80.441, 81.795},
                                {35.062, 41.315, 47.265, 54.133, 61.250, 68.480, 75.346, 79.914, 81.325},
                                {34.038, 40.254, 46.185, 53.066, 60.250, 67.607, 74.655, 79.408, 80.879},
                                {29.704, 35.679, 41.448, 48.276, 55.634, 63.479, 71.375, 77.129, 78.941},
                                {26.339, 31.999, 37.521, 44.162, 51.501, 59.617, 68.237, 75.108, 77.358},
                                {23.638, 28.930, 34.148, 40.517, 47.713, 55.935, 65.125, 73.200, 76.019},
                                {21.411, 26.304, 31.182, 37.224, 44.194, 52.395, 61.999, 71.296, 74.859},
                                {19.531, 24.013, 28.535, 34.219, 40.906, 48.992, 58.862, 69.318, 73.836},
                                {17.910, 21.986, 26.151, 31.464, 37.834, 45.734, 55.739, 67.213, 72.921},
                                {16.485, 20.173, 23.991, 28.936, 34.972, 42.632, 52.661, 64.966, 72.093},
                                {15.211, 18.536, 22.027, 26.616, 32.314, 39.698, 49.656, 62.591, 71.337},
                                {14.051, 17.044, 20.233, 24.486, 29.852, 36.938, 46.748, 60.122, 70.642},
                                {12.982, 15.675, 18.588, 22.530, 27.578, 34.354, 43.955, 57.601, 69.998},
                                {11.982, 14.407, 17.071, 20.730, 25.477, 31.941, 41.287, 55.065, 69.399},
                                {11.037, 13.223, 15.666, 19.068, 23.536, 29.694, 38.752, 52.542, 68.838},
                                {10.136, 12.111, 14.357, 17.527, 21.739, 27.602, 36.351, 50.056, 68.312},
                                {9.269, 11.059, 13.129, 16.093, 20.070, 25.654, 34.083, 47.624, 67.815},
                                {8.429, 10.056, 11.972, 14.751, 18.515, 23.837, 31.944, 45.257, 67.346},
                                {7.612, 9.095, 10.874, 13.489, 17.061, 22.138, 29.928, 42.964, 66.900},
                                {6.030, 7.273, 8.825, 11.164, 14.407, 19.050, 26.235, 38.617, 66.072},
                                {4.498, 5.556, 6.929, 9.049, 12.026, 16.304, 22.941, 34.601, 65.316},
                                {3.004, 3.915, 5.147, 7.093, 9.855, 13.830, 19.982, 30.910, 64.621},
                                {1.541, 2.336, 3.455, 5.261, 7.848, 11.571, 17.302, 27.523, 63.977},
                                {0.103, 0.805, 1.834, 3.528, 5.972, 9.484, 14.854, 24.413, 63.378},
                                {-1.311, -0.684, 0.272, 1.873, 4.200, 7.538, 12.597, 21.550, 62.818},
                                {-2.702, -2.137, -1.241, 0.285, 2.516, 5.707, 10.500, 18.905, 62.291},
                                {-4.070, -3.557, -2.710, -1.246, 0.904, 3.972, 8.537, 16.452, 61.795},
                                {-5.417, -4.945, -4.140, -2.728, -0.646, 2.319, 6.689, 14.166, 61.325},
                                {-6.741, -6.305, -5.534, -4.166, -2.141, 0.736, 4.938, 12.027, 60.879},
                                {-9.955, -9.585, -8.880, -7.594, -5.677, -2.969, 0.905, 7.208, 59.856},
                                {-13.033, -12.709, -12.047, -10.819, -8.976, -6.385, -2.743, 2.977, 58.941},
                                {-15.981, -15.689, -15.059, -13.871, -12.081, -9.573, -6.099, -0.816, 58.113},
                                {-18.809, -18.541, -17.934, -16.774, -15.023, -12.577, -9.227, -4.275, 57.358},
                                {-21.529, -21.277, -20.688, -19.550, -17.827, -15.427, -12.172, -7.473, 56.662},
                                {-24.151, -23.913, -23.336, -22.214, -20.514, -18.150, -14.966, -10.466, 56.019},
                                {-26.687, -26.459, -25.893, -24.784, -23.101, -20.764, -17.637, -13.294, 55.419},
                                {-29.150, -28.930, -28.371, -27.273, -25.603, -23.288, -20.207, -15.988, 54.859},
                                {-31.550, -31.336, -30.784, -29.694, -28.035, -25.737, -22.692, -18.575, 54.332},
                                {-33.896, -33.688, -33.141, -32.057, -30.407, -28.124, -25.108, -21.074, 53.836},
                                {-36.198, -35.995, -35.452, -34.374, -32.730, -30.459, -27.467, -23.501, 53.366},
                                {-38.464, -38.264, -37.724, -36.651, -35.013, -32.752, -29.780, -25.872, 52.921},
                                {-40.700, -40.503, -39.966, -38.896, -37.264, -35.010, -32.055, -28.195, 52.497},
                                {-42.911, -42.717, -42.183, -41.116, -39.488, -37.241, -34.301, -30.481, 52.093},
                                {-45.104, -44.912, -44.379, -43.315, -41.691, -39.450, -36.522, -32.737, 51.707},
                                {-47.281, -47.090, -46.560, -45.498, -43.877, -41.641, -38.723, -34.968, 51.337},
                                {-49.445, -49.256, -48.727, -47.667, -46.049, -43.817, -40.908, -37.178, 50.982},
                                {-51.598, -51.411, -50.883, -49.825, -48.209, -45.981, -43.080, -39.373, 50.642},
                                {-53.743, -53.556, -53.030, -51.974, -50.359, -48.135, -45.240, -41.552, 50.314},
                                {-55.878, -55.693, -55.168, -54.113, -52.500, -50.278, -47.390, -43.719, 49.998},
                                {-58.005, -57.821, -57.297, -56.243, -54.632, -52.412, -49.529, -45.873, 49.693},
                                {-60.123, -59.939, -59.416, -58.363, -56.753, -54.536, -51.657, -48.015, 49.399},
                                {-62.230, -62.047, -61.524, -60.472, -58.864, -56.649, -53.774, -50.144, 49.114},
                                {-64.325, -64.143, -63.620, -62.569, -60.962, -58.748, -55.877, -52.258, 48.838},
                                {-66.405, -66.224, -65.702, -64.652, -63.045, -60.834, -57.966, -54.355, 48.571},
                                {-68.469, -68.288, -67.767, -66.717, -65.112, -62.901, -60.037, -56.435, 48.312},
                                {-70.514, -70.334, -69.813, -68.764, -67.159, -64.950, -62.088, -58.493, 48.060},
                                {-72.537, -72.356, -71.836, -70.787, -69.183, -66.975, -64.116, -60.528, 47.815},
                                {-74.534, -74.354, -73.834, -72.786, -71.182, -68.975, -66.118, -62.537, 47.577},
                                {-76.502, -76.323, -75.803, -74.755, -73.152, -70.946, -68.091, -64.515, 47.346},
                                {-78.439, -78.259, -77.740, -76.693, -75.090, -72.885, -70.031, -66.461, 47.120},
                                {-80.340, -80.161, -79.642, -78.595, -76.993, -74.789, -71.937, -68.371, 46.900},
                        },
                        {// 2000 MHz 50%
                                {94.233, 96.509, 98.662, 101.148, 103.509, 105.319, 106.328, 106.732, 106.900},
                                {82.427, 85.910, 88.758, 91.971, 95.244, 98.116, 99.916, 100.632, 100.879},
                                {74.501, 79.135, 82.671, 86.395, 90.171, 93.677, 96.070, 97.049, 97.358},
                                {68.368, 73.847, 78.078, 82.308, 86.474, 90.429, 93.289, 94.498, 94.859},
                                {63.385, 69.412, 74.253, 79.006, 83.536, 87.851, 91.099, 92.513, 92.921},
                                {59.209, 65.580, 70.908, 76.172, 81.068, 85.701, 89.284, 90.887, 91.337},
                                {55.628, 62.216, 67.909, 73.643, 78.912, 83.845, 87.729, 89.509, 89.998},
                                {52.499, 59.227, 65.186, 71.329, 76.970, 82.198, 86.365, 88.312, 88.838},
                                {49.725, 56.544, 62.696, 69.181, 75.181, 80.707, 85.144, 87.253, 87.815},
                                {47.236, 54.116, 60.406, 67.169, 73.507, 79.331, 84.035, 86.304, 86.900},
                                {44.981, 51.901, 58.291, 65.277, 71.922, 78.043, 83.013, 85.442, 86.072},
                                {42.922, 49.867, 56.329, 63.490, 70.408, 76.822, 82.061, 84.652, 85.316},
                                {41.029, 47.989, 54.502, 61.800, 68.956, 75.653, 81.165, 83.922, 84.621},
                                {39.279, 46.245, 52.794, 60.198, 67.558, 74.524, 80.313, 83.243, 83.977},
                                {37.653, 44.619, 51.191, 58.677, 66.210, 73.429, 79.497, 82.608, 83.378},
                                {36.137, 43.096, 49.682, 57.231, 64.909, 72.361, 78.708, 82.009, 82.818},
                                {34.717, 41.665, 48.257, 55.853, 63.652, 71.316, 77.942, 81.442, 82.291},
                                {33.384, 40.316, 46.908, 54.537, 62.437, 70.293, 77.192, 80.903, 81.795},
                                {32.129, 39.041, 45.627, 53.278, 61.260, 69.289, 76.455, 80.387, 81.325},
                                {30.945, 37.832, 44.407, 52.072, 60.121, 68.303, 75.728, 79.893, 80.879},
                                {25.889, 32.596, 39.051, 46.684, 54.906, 63.628, 72.179, 77.642, 78.941},
                                {21.921, 28.355, 34.599, 42.081, 50.306, 59.317, 68.706, 75.604, 77.358},
                                {18.729, 24.802, 30.756, 37.994, 46.119, 55.280, 65.291, 73.633, 76.019},
                                {16.114, 21.754, 27.352, 34.269, 42.210, 51.427, 61.921, 71.641, 74.859},
                                {13.939, 19.099, 24.292, 30.826, 38.510, 47.703, 58.580, 69.571, 73.836},
                                {12.108, 16.766, 21.526, 27.634, 34.999, 44.085, 55.252, 67.388, 72.921},
                                {10.548, 14.707, 19.028, 24.685, 31.681, 40.581, 51.937, 65.076, 72.093},
                                {9.201, 12.884, 16.779, 21.982, 28.576, 37.214, 48.650, 62.636, 71.337},
                                {8.025, 11.268, 14.761, 19.525, 25.699, 34.012, 45.413, 60.085, 70.642},
                                {6.984, 9.831, 12.957, 17.305, 23.060, 31.001, 42.256, 57.448, 69.998},
                                {6.050, 8.546, 11.343, 15.308, 20.658, 28.196, 39.207, 54.753, 69.399},
                                {5.200, 7.390, 9.896, 13.516, 18.482, 25.607, 36.292, 52.029, 68.838},
                                {4.416, 6.342, 8.594, 11.905, 16.516, 23.230, 33.529, 49.305, 68.312},
                                {3.683, 5.381, 7.413, 10.452, 14.740, 21.057, 30.929, 46.606, 67.815},
                                {2.990, 4.493, 6.334, 9.135, 13.131, 19.074, 28.496, 43.955, 67.346},
                                {2.327, 3.662, 5.339, 7.933, 11.669, 17.262, 26.230, 41.369, 66.900},
                                {1.061, 2.131, 3.544, 5.800, 9.102, 14.082, 22.170, 36.454, 66.072},
                                {-0.158, 0.716, 1.934, 3.934, 6.899, 11.377, 18.668, 31.936, 65.316},
                                {-1.356, -0.628, 0.443, 2.249, 4.953, 9.027, 15.628, 27.838, 64.621},
                                {-2.550, -1.932, -0.972, 0.686, 3.187, 6.939, 12.958, 24.145, 63.977},
                                {-3.746, -3.213, -2.338, -0.794, 1.548, 5.042, 10.577, 20.821, 63.378},
                                {-4.950, -4.482, -3.674, -2.219, -0.002, 3.287, 8.421, 17.823, 62.818},
                                {-6.161, -5.745, -4.989, -3.604, -1.486, 1.637, 6.440, 15.102, 62.291},
                                {-7.380, -7.004, -6.290, -4.962, -2.923, 0.066, 4.597, 12.617, 61.795},
                                {-8.604, -8.261, -7.581, -6.297, -4.322, -1.442, 2.863, 10.329, 61.325},
                                {-9.831, -9.515, -8.861, -7.614, -5.692, -2.901, 1.216, 8.208, 60.879},
                                {-12.895, -12.626, -12.020, -10.839, -9.011, -6.382, -2.610, 3.469, 59.856},
                                {-15.923, -15.685, -15.110, -13.970, -12.202, -9.678, -6.132, -0.679, 58.941},
                                {-18.888, -18.670, -18.115, -17.003, -15.275, -12.821, -9.429, -4.412, 58.113},
                                {-21.771, -21.567, -21.026, -19.933, -18.232, -15.826, -12.542, -7.834, 57.358},
                                {-24.562, -24.368, -23.837, -22.758, -21.077, -18.705, -15.497, -11.013, 56.662},
                                {-27.260, -27.073, -26.549, -25.479, -23.813, -21.466, -18.314, -13.996, 56.019},
                                {-29.866, -29.684, -29.166, -28.103, -26.447, -24.120, -21.009, -16.816, 55.419},
                                {-32.387, -32.209, -31.695, -30.638, -28.989, -26.676, -23.597, -19.500, 54.859},
                                {-34.830, -34.656, -34.144, -33.092, -31.450, -29.147, -26.093, -22.070, 54.332},
                                {-37.206, -37.034, -36.525, -35.476, -33.839, -31.545, -28.510, -24.545, 53.836},
                                {-39.525, -39.355, -38.848, -37.801, -36.168, -33.881, -30.861, -26.943, 53.366},
                                {-41.798, -41.629, -41.123, -40.079, -38.449, -36.167, -33.160, -29.279, 52.921},
                                {-44.034, -43.866, -43.362, -42.319, -40.691, -38.414, -35.417, -31.566, 52.497},
                                {-46.243, -46.077, -45.574, -44.532, -42.906, -40.633, -37.644, -33.818, 52.093},
                                {-48.435, -48.269, -47.767, -46.727, -45.103, -42.832, -39.850, -36.045, 51.707},
                                {-50.617, -50.452, -49.951, -48.911, -47.289, -45.021, -42.044, -38.257, 51.337},
                                {-52.796, -52.631, -52.131, -51.092, -49.471, -47.205, -44.233, -40.460, 50.982},
                                {-54.976, -54.812, -54.312, -53.274, -51.654, -49.389, -46.422, -42.661, 50.642},
                                {-57.162, -56.998, -56.498, -55.461, -53.841, -51.579, -48.614, -44.864, 50.314},
                                {-59.354, -59.190, -58.691, -57.654, -56.035, -53.774, -50.812, -47.072, 49.998},
                                {-61.552, -61.389, -60.890, -59.854, -58.236, -55.975, -53.016, -49.284, 49.693},
                                {-63.756, -63.593, -63.094, -62.058, -60.440, -58.181, -55.224, -51.498, 49.399},
                                {-65.960, -65.797, -65.298, -64.263, -62.646, -60.387, -57.433, -53.712, 49.114},
                                {-68.159, -67.997, -67.498, -66.463, -64.846, -62.589, -59.636, -55.920, 48.838},
                                {-70.347, -70.184, -69.686, -68.651, -67.035, -64.778, -61.826, -58.115, 48.571},
                                {-72.513, -72.351, -71.853, -70.818, -69.202, -66.946, -63.995, -60.289, 48.312},
                                {-74.649, -74.487, -73.989, -72.954, -71.339, -69.083, -66.134, -62.431, 48.060},
                                {-76.744, -76.582, -76.084, -75.049, -73.434, -71.179, -68.230, -64.530, 47.815},
                                {-78.785, -78.624, -78.126, -77.092, -75.476, -73.221, -70.274, -66.577, 47.577},
                                {-80.763, -80.602, -80.104, -79.070, -77.455, -75.200, -72.254, -68.559, 47.346},
                                {-82.667, -82.505, -82.008, -80.973, -79.359, -77.104, -74.159, -70.466, 47.120},
                                {-84.485, -84.324, -83.826, -82.792, -81.178, -78.924, -75.979, -72.288, 46.900},
                        },

                },
        };
    }

    ;

    private static final class tabDataSea {


        private static final double[][][][] get = new double[][][][]{
                {
                        { //100 MHz, 1%, Cold Sea, Fig6
                                {101.483, 103.915, 106.062, 107.06, 107.251, 107.266, 107.266, 107.266},
                                {90.908, 93.933, 97.48, 100.441, 101.485, 101.607, 101.612, 101.613},
                                {84.475, 87.628, 91.529, 95.67, 98.054, 98.48, 98.506, 98.507},
                                {79.875, 83.064, 87.047, 91.664, 95.299, 96.283, 96.317, 96.317},
                                {76.277, 79.479, 83.473, 88.263, 92.785, 94.537, 94.653, 94.653},
                                {73.298, 76.51, 80.501, 85.347, 90.415, 93.028, 93.314, 93.314},
                                {70.736, 73.963, 77.951, 82.811, 88.193, 91.64, 92.194, 92.194},
                                {68.474, 71.72, 75.712, 80.574, 86.13, 90.312, 91.229, 91.229},
                                {66.436, 69.71, 73.712, 78.573, 84.226, 89.011, 90.337, 90.381},
                                {64.574, 67.881, 71.901, 76.765, 82.471, 87.729, 89.513, 89.622},
                                {62.856, 66.202, 70.245, 75.115, 80.851, 86.466, 88.741, 88.934},
                                {61.259, 64.647, 68.718, 73.599, 79.351, 85.23, 88.006, 88.303},
                                {59.767, 63.2, 67.303, 72.196, 77.957, 84.027, 87.296, 87.705},
                                {58.369, 61.848, 65.984, 70.891, 76.657, 82.864, 86.601, 87.146},
                                {57.057, 60.582, 64.751, 69.674, 75.441, 81.743, 85.915, 86.62},
                                {55.832, 59.4, 63.598, 68.533, 74.298, 80.665, 85.233, 86.122},
                                {54.686, 58.292, 62.517, 67.462, 73.222, 79.63, 84.551, 85.647},
                                {53.619, 57.257, 61.503, 66.455, 72.205, 78.637, 83.869, 85.193},
                                {52.746, 56.378, 60.611, 65.542, 71.265, 77.702, 83.199, 84.758},
                                {51.721, 55.399, 59.665, 64.611, 70.328, 76.767, 82.499, 84.332},
                                {48.389, 51.952, 56.077, 60.853, 66.375, 72.683, 79.118, 82.357},
                                {46.275, 49.568, 53.414, 57.903, 63.139, 69.21, 75.859, 80.454},
                                {45.095, 48.005, 51.464, 55.576, 60.463, 66.249, 72.891, 78.548},
                                {44.308, 46.843, 49.92, 53.656, 58.19, 63.682, 70.213, 76.614},
                                {43.705, 45.908, 48.64, 52.029, 56.231, 61.434, 67.805, 74.676},
                                {43.187, 45.11, 47.544, 50.624, 54.522, 59.451, 65.636, 72.766},
                                {42.706, 44.399, 46.581, 49.394, 53.02, 57.691, 63.676, 70.915},
                                {42.234, 43.743, 45.718, 48.305, 51.692, 56.124, 61.903, 69.139},
                                {41.757, 43.121, 44.93, 47.329, 50.51, 54.724, 60.295, 67.449},
                                {41.267, 42.521, 44.2, 46.446, 49.451, 53.47, 58.836, 65.85},
                                {40.763, 41.935, 43.513, 45.636, 48.495, 52.341, 57.509, 64.344},
                                {40.246, 41.358, 42.86, 44.887, 47.624, 51.318, 56.298, 62.93},
                                {39.721, 40.79, 42.235, 44.186, 46.822, 50.382, 55.186, 61.606},
                                {39.193, 40.231, 41.632, 43.524, 46.075, 49.518, 54.159, 60.368},
                                {38.665, 39.682, 41.05, 42.892, 45.373, 48.712, 53.203, 59.209},
                                {38.144, 39.143, 40.484, 42.285, 44.704, 47.951, 52.308, 58.124},
                                {37.124, 38.096, 39.393, 41.127, 43.441, 46.531, 50.655, 56.14},
                                {36.138, 37.085, 38.344, 40.019, 42.245, 45.205, 49.14, 54.355},
                                {35.18, 36.101, 37.323, 38.944, 41.093, 43.942, 47.719, 52.716},
                                {34.24, 35.135, 36.32, 37.89, 39.969, 42.722, 46.366, 51.183},
                                {33.311, 34.18, 35.33, 36.852, 38.867, 41.534, 45.063, 49.728},
                                {32.387, 33.23, 34.347, 35.825, 37.782, 40.371, 43.798, 48.328},
                                {31.462, 32.284, 33.371, 34.809, 36.712, 39.23, 42.562, 46.966},
                                {30.547, 31.345, 32.402, 33.801, 35.655, 38.108, 41.357, 45.655},
                                {29.636, 30.412, 31.44, 32.803, 34.609, 37.004, 40.177, 44.382},
                                {28.73, 29.484, 30.485, 31.813, 33.576, 35.916, 39.021, 43.14},
                                {26.487, 27.193, 28.133, 29.384, 31.05, 33.268, 36.221, 40.151},
                                {24.285, 24.95, 25.838, 27.023, 28.606, 30.719, 33.541, 37.31},
                                {22.133, 22.762, 23.604, 24.732, 26.243, 28.265, 30.974, 34.601},
                                {20.034, 20.632, 21.434, 22.512, 23.959, 25.901, 28.51, 32.011},
                                {17.989, 18.559, 19.326, 20.359, 21.75, 23.621, 26.14, 29.53},
                                {15.994, 16.547, 17.292, 18.294, 19.644, 21.459, 23.903, 27.192},
                                {14.057, 14.581, 15.288, 16.245, 17.538, 19.287, 21.653, 24.851},
                                {12.168, 12.671, 13.352, 14.276, 15.528, 17.224, 19.523, 22.638},
                                {10.327, 10.811, 11.469, 12.363, 13.576, 15.224, 17.461, 20.5},
                                {8.534, 9.001, 9.637, 10.503, 11.681, 13.284, 15.466, 18.434},
                                {6.788, 7.24, 7.856, 8.696, 9.842, 11.403, 13.532, 16.435},
                                {5.089, 5.526, 6.124, 6.94, 8.056, 9.579, 11.66, 14.502},
                                {3.437, 3.861, 4.442, 5.236, 6.323, 7.81, 9.846, 12.631},
                                {1.832, 2.244, 2.809, 3.582, 4.643, 6.097, 8.09, 10.822},
                                {0.276, 0.676, 1.225, 1.98, 3.015, 4.438, 6.391, 9.074},
                                {-1.232, -0.843, -0.308, 0.428, 1.441, 2.834, 4.749, 7.385},
                                {-2.691, -2.313, -1.791, -1.072, -0.081, 1.284, 3.164, 5.755},
                                {-4.102, -3.733, -3.223, -2.52, -1.551, -0.212, 1.635, 4.184},
                                {-5.462, -5.102, -4.605, -3.917, -2.967, -1.654, 0.162, 2.671},
                                {-6.773, -6.422, -5.936, -5.263, -4.331, -3.041, -1.256, 1.216},
                                {-8.034, -7.691, -7.215, -6.556, -5.642, -4.375, -2.619, -0.183},
                                {-9.245, -8.91, -8.445, -7.799, -6.902, -5.656, -3.927, -1.525},
                                {-10.407, -10.079, -9.624, -8.99, -8.109, -6.884, -5.181, -2.812},
                                {-11.52, -11.199, -10.753, -10.131, -9.266, -8.06, -6.382, -4.044},
                                {-12.584, -12.27, -11.833, -11.222, -10.372, -9.185, -7.53, -5.223},
                                {-13.601, -13.293, -12.864, -12.265, -11.428, -10.26, -8.628, -6.349},
                                {-14.57, -14.269, -13.848, -13.259, -12.436, -11.285, -9.675, -7.424},
                                {-15.494, -15.199, -14.786, -14.207, -13.397, -12.262, -10.673, -8.449},
                                {-16.373, -16.084, -15.678, -15.109, -14.311, -13.192, -11.624, -9.425},
                                {-17.209, -16.925, -16.526, -15.966, -15.181, -14.077, -12.528, -10.354},
                                {-18.002, -17.723, -17.332, -16.781, -16.007, -14.918, -13.388, -11.237},
                                {-18.755, -18.481, -18.096, -17.554, -16.791, -15.717, -14.205, -12.077}
                        },
                        {//600 MHz, 1%, Cold Sea, Fig14
                                {107.073, 107.208, 107.262, 107.266, 107.266, 107.266, 107.266, 107.266},
                                {100.311, 101.201, 101.593, 101.612, 101.613, 101.613, 101.613, 101.613},
                                {95.293, 97.276, 98.437, 98.506, 98.507, 98.507, 98.507, 98.507},
                                {91.161, 93.962, 96.157, 96.317, 96.317, 96.317, 96.317, 96.317},
                                {87.749, 91.04, 94.236, 94.653, 94.653, 94.653, 94.653, 94.653},
                                {84.887, 88.465, 92.437, 93.314, 93.314, 93.314, 93.314, 93.314},
                                {82.437, 86.195, 90.673, 92.194, 92.194, 92.194, 92.194, 92.194},
                                {80.301, 84.181, 88.941, 91.229, 91.229, 91.229, 91.229, 91.229},
                                {78.41, 82.378, 87.271, 90.344, 90.381, 90.381, 90.381, 90.381},
                                {76.715, 80.752, 85.691, 89.502, 89.622, 89.622, 89.622, 89.622},
                                {75.18, 79.272, 84.215, 88.691, 88.934, 88.934, 88.934, 88.934},
                                {73.777, 77.915, 82.846, 87.887, 88.304, 88.304, 88.304, 88.304},
                                {72.486, 76.664, 81.579, 87.076, 87.718, 87.72, 87.72, 87.72},
                                {71.29, 75.504, 80.407, 86.249, 87.164, 87.17, 87.17, 87.17},
                                {70.176, 74.422, 79.321, 85.407, 86.644, 86.654, 86.654, 86.654},
                                {69.134, 73.408, 78.313, 84.555, 86.154, 86.17, 86.17, 86.17},
                                {68.154, 72.456, 77.374, 83.703, 85.687, 85.712, 85.712, 85.712},
                                {67.231, 71.557, 76.499, 82.861, 85.241, 85.279, 85.279, 85.279},
                                {66.419, 70.744, 75.68, 82.037, 84.811, 84.867, 84.867, 84.867},
                                {65.696, 70, 74.911, 81.237, 84.393, 84.475, 84.475, 84.475},
                                {62.888, 66.997, 71.679, 77.714, 82.401, 82.738, 82.738, 82.738},
                                {60.824, 64.742, 69.203, 74.955, 80.422, 81.26, 81.26, 81.26},
                                {59.256, 63.005, 67.271, 72.774, 78.456, 79.982, 79.982, 79.982},
                                {58.126, 61.697, 65.761, 71.003, 76.606, 78.856, 78.856, 78.856},
                                {57.364, 60.739, 64.58, 69.535, 74.931, 77.79, 77.853, 77.853},
                                {56.873, 60.043, 63.652, 68.306, 73.442, 76.745, 76.949, 76.949},
                                {56.553, 59.525, 62.909, 67.272, 72.124, 75.722, 76.128, 76.128},
                                {56.33, 59.12, 62.299, 66.397, 70.958, 74.717, 75.376, 75.376},
                                {56.155, 58.785, 61.783, 65.647, 69.927, 73.733, 74.682, 74.682},
                                {56, 58.491, 61.333, 64.993, 69.012, 72.772, 74.029, 74.04},
                                {55.85, 58.22, 60.927, 64.411, 68.196, 71.843, 73.361, 73.441},
                                {55.695, 57.962, 60.551, 63.883, 67.46, 70.953, 72.711, 72.853},
                                {55.532, 57.707, 60.194, 63.393, 66.789, 70.109, 72.074, 72.284},
                                {55.359, 57.453, 59.85, 62.93, 66.17, 69.313, 71.441, 71.741},
                                {55.174, 57.196, 59.512, 62.487, 65.589, 68.567, 70.806, 71.221},
                                {54.978, 56.934, 59.178, 62.057, 65.039, 67.866, 70.166, 70.72},
                                {54.549, 56.39, 58.506, 61.218, 63.998, 66.577, 68.868, 69.766},
                                {54.076, 55.817, 57.822, 60.387, 63.004, 65.398, 67.576, 68.853},
                                {53.563, 55.214, 57.118, 59.552, 62.03, 64.285, 66.328, 67.947},
                                {53.014, 54.582, 56.392, 58.705, 61.061, 63.207, 65.132, 66.995},
                                {52.434, 53.924, 55.646, 57.844, 60.089, 62.144, 63.975, 65.951},
                                {51.827, 53.243, 54.882, 56.972, 59.111, 61.084, 62.84, 64.817},
                                {51.196, 52.543, 54.101, 56.088, 58.127, 60.022, 61.712, 63.618},
                                {50.545, 51.825, 53.306, 55.194, 57.137, 58.955, 60.582, 62.382},
                                {49.875, 51.091, 52.498, 54.292, 56.142, 57.882, 59.445, 61.123},
                                {49.189, 50.344, 51.68, 53.383, 55.14, 56.803, 58.298, 59.848},
                                {47.414, 48.424, 49.589, 51.076, 52.611, 54.073, 55.377, 56.593},
                                {45.565, 46.436, 47.439, 48.721, 50.036, 51.282, 52.362, 53.241},
                                {43.657, 44.392, 45.235, 46.316, 47.407, 48.419, 49.247, 49.834},
                                {41.702, 42.303, 42.991, 43.874, 44.738, 45.506, 46.085, 46.463},
                                {39.708, 40.185, 40.73, 41.429, 42.082, 42.627, 43, 43.249},
                                {37.646, 38.036, 38.482, 39.055, 39.566, 39.969, 40.223, 40.401},
                                {35.549, 35.891, 36.281, 36.783, 37.221, 37.556, 37.76, 37.905},
                                {33.511, 33.812, 34.154, 34.595, 34.972, 35.253, 35.422, 35.545},
                                {31.554, 31.82, 32.122, 32.512, 32.84, 33.079, 33.222, 33.33},
                                {29.69, 29.927, 30.197, 30.544, 30.832, 31.039, 31.164, 31.261},
                                {27.924, 28.138, 28.38, 28.693, 28.948, 29.131, 29.242, 29.333},
                                {26.255, 26.449, 26.669, 26.953, 27.183, 27.346, 27.448, 27.533},
                                {24.676, 24.853, 25.054, 25.314, 25.524, 25.672, 25.767, 25.849},
                                {23.177, 23.34, 23.526, 23.766, 23.958, 24.094, 24.184, 24.263},
                                {21.747, 21.898, 22.071, 22.293, 22.471, 22.598, 22.684, 22.76},
                                {20.374, 20.516, 20.676, 20.884, 21.05, 21.169, 21.252, 21.326},
                                {19.048, 19.18, 19.331, 19.526, 19.681, 19.793, 19.873, 19.946},
                                {17.758, 17.883, 18.024, 18.208, 18.353, 18.46, 18.538, 18.61},
                                {16.497, 16.614, 16.748, 16.921, 17.059, 17.16, 17.236, 17.308},
                                {15.258, 15.369, 15.496, 15.66, 15.79, 15.887, 15.962, 16.033},
                                {14.038, 14.144, 14.264, 14.419, 14.543, 14.636, 14.709, 14.779},
                                {12.834, 12.935, 13.049, 13.196, 13.314, 13.404, 13.476, 13.546},
                                {11.647, 11.742, 11.851, 11.992, 12.105, 12.191, 12.263, 12.332},
                                {10.477, 10.569, 10.673, 10.807, 10.915, 10.999, 11.07, 11.139},
                                {9.328, 9.416, 9.516, 9.644, 9.748, 9.83, 9.9, 9.969},
                                {8.204, 8.289, 8.385, 8.508, 8.608, 8.688, 8.758, 8.827},
                                {7.111, 7.192, 7.284, 7.404, 7.501, 7.578, 7.648, 7.717},
                                {6.052, 6.131, 6.221, 6.336, 6.43, 6.506, 6.575, 6.644},
                                {5.036, 5.112, 5.199, 5.311, 5.403, 5.478, 5.547, 5.615},
                                {4.067, 4.141, 4.226, 4.335, 4.424, 4.498, 4.567, 4.636},
                                {3.151, 3.223, 3.306, 3.412, 3.5, 3.573, 3.642, 3.71},
                                {2.293, 2.364, 2.444, 2.549, 2.635, 2.708, 2.776, 2.845},
                        },
                        {//2000 MHz, 1%, Cold Sea, Fig22
                                {107.266, 107.266, 107.266, 107.266, 107.266, 107.266, 107.266, 107.266},
                                {101.613, 101.613, 101.613, 101.613, 101.613, 101.613, 101.613, 101.613},
                                {98.507, 98.507, 98.507, 98.507, 98.507, 98.507, 98.507, 98.507},
                                {96.317, 96.317, 96.317, 96.317, 96.317, 96.317, 96.317, 96.317},
                                {94.653, 94.653, 94.653, 94.653, 94.653, 94.653, 94.653, 94.653},
                                {93.314, 93.314, 93.314, 93.314, 93.314, 93.314, 93.314, 93.314},
                                {92.194, 92.194, 92.194, 92.194, 92.194, 92.194, 92.194, 92.194},
                                {91.229, 91.229, 91.229, 91.229, 91.229, 91.229, 91.229, 91.229},
                                {90.381, 90.381, 90.381, 90.381, 90.381, 90.381, 90.381, 90.381},
                                {89.622, 89.622, 89.622, 89.622, 89.622, 89.622, 89.622, 89.622},
                                {88.934, 88.934, 88.934, 88.934, 88.934, 88.934, 88.934, 88.934},
                                {88.304, 88.304, 88.304, 88.304, 88.304, 88.304, 88.304, 88.304},
                                {87.72, 87.72, 87.72, 87.72, 87.72, 87.72, 87.72, 87.72},
                                {87.17, 87.17, 87.17, 87.17, 87.17, 87.17, 87.17, 87.17},
                                {86.654, 86.654, 86.654, 86.654, 86.654, 86.654, 86.654, 86.654},
                                {86.17, 86.17, 86.17, 86.17, 86.17, 86.17, 86.17, 86.17},
                                {85.712, 85.712, 85.712, 85.712, 85.712, 85.712, 85.712, 85.712},
                                {85.279, 85.279, 85.279, 85.279, 85.279, 85.279, 85.279, 85.279},
                                {84.867, 84.867, 84.867, 84.867, 84.867, 84.867, 84.867, 84.867},
                                {84.475, 84.475, 84.475, 84.475, 84.475, 84.475, 84.475, 84.475},
                                {82.738, 82.738, 82.738, 82.738, 82.738, 82.738, 82.738, 82.738},
                                {81.26, 81.26, 81.26, 81.26, 81.26, 81.26, 81.26, 81.26},
                                {79.982, 79.982, 79.982, 79.982, 79.982, 79.982, 79.982, 79.982},
                                {78.856, 78.856, 78.856, 78.856, 78.856, 78.856, 78.856, 78.856},
                                {77.853, 77.853, 77.853, 77.853, 77.853, 77.853, 77.853, 77.853},
                                {76.949, 76.949, 76.949, 76.949, 76.949, 76.949, 76.949, 76.949},
                                {76.128, 76.128, 76.128, 76.128, 76.128, 76.128, 76.128, 76.128},
                                {75.376, 75.376, 75.376, 75.376, 75.376, 75.376, 75.376, 75.376},
                                {74.682, 74.682, 74.682, 74.682, 74.682, 74.682, 74.682, 74.682},
                                {74.04, 74.04, 74.04, 74.04, 74.04, 74.04, 74.04, 74.04},
                                {73.417, 73.417, 73.417, 73.417, 73.417, 73.417, 73.417, 73.417},
                                {72.81, 72.81, 72.81, 72.81, 72.81, 72.81, 72.81, 72.81},
                                {72.231, 72.231, 72.231, 72.231, 72.231, 72.231, 72.231, 72.231},
                                {71.677, 71.677, 71.677, 71.677, 71.677, 71.677, 71.677, 71.677},
                                {71.145, 71.145, 71.145, 71.145, 71.145, 71.145, 71.145, 71.145},
                                {70.632, 70.632, 70.632, 70.632, 70.632, 70.632, 70.632, 70.632},
                                {69.656, 69.656, 69.656, 69.656, 69.656, 69.656, 69.656, 69.656},
                                {68.735, 68.735, 68.735, 68.735, 68.735, 68.735, 68.735, 68.735},
                                {67.859, 67.859, 67.859, 67.859, 67.859, 67.859, 67.859, 67.859},
                                {67.019, 67.019, 67.019, 67.019, 67.019, 67.019, 67.019, 67.019},
                                {66.207, 66.207, 66.207, 66.207, 66.207, 66.207, 66.207, 66.207},
                                {65.42, 65.42, 65.42, 65.42, 65.42, 65.42, 65.42, 65.42},
                                {64.653, 64.653, 64.653, 64.653, 64.653, 64.653, 64.653, 64.653},
                                {63.904, 63.904, 63.904, 63.904, 63.904, 63.904, 63.904, 63.904},
                                {63.17, 63.17, 63.17, 63.17, 63.17, 63.17, 63.17, 63.17},
                                {62.452, 62.452, 62.452, 62.452, 62.452, 62.452, 62.452, 62.452},
                                {60.713, 60.713, 60.713, 60.713, 60.713, 60.713, 60.713, 60.713},
                                {59.042, 59.042, 59.042, 59.042, 59.042, 59.042, 59.042, 59.042},
                                {57.413, 57.413, 57.413, 57.413, 57.413, 57.413, 57.413, 57.413},
                                {55.795, 55.795, 55.795, 55.795, 55.795, 55.795, 55.795, 55.795},
                                {54.159, 54.159, 54.159, 54.159, 54.159, 54.159, 54.159, 54.159},
                                {52.487, 52.487, 52.487, 52.487, 52.487, 52.487, 52.487, 52.487},
                                {50.772, 50.772, 50.772, 50.772, 50.772, 50.772, 50.772, 50.772},
                                {49.018, 49.018, 49.018, 49.018, 49.018, 49.018, 49.018, 49.018},
                                {47.237, 47.237, 47.237, 47.237, 47.237, 47.237, 47.237, 47.237},
                                {45.439, 45.439, 45.439, 45.439, 45.439, 45.439, 45.439, 45.439},
                                {43.637, 43.637, 43.637, 43.637, 43.637, 43.637, 43.637, 43.637},
                                {41.836, 41.836, 41.836, 41.836, 41.836, 41.836, 41.836, 41.836},
                                {40.043, 40.043, 40.043, 40.043, 40.043, 40.043, 40.043, 40.043},
                                {38.257, 38.257, 38.257, 38.257, 38.257, 38.257, 38.257, 38.257},
                                {36.479, 36.479, 36.479, 36.479, 36.479, 36.479, 36.479, 36.479},
                                {34.706, 34.706, 34.706, 34.706, 34.706, 34.706, 34.706, 34.706},
                                {32.935, 32.935, 32.935, 32.935, 32.935, 32.935, 32.935, 32.935},
                                {31.164, 31.164, 31.164, 31.164, 31.164, 31.164, 31.164, 31.164},
                                {29.391, 29.391, 29.391, 29.391, 29.391, 29.391, 29.391, 29.391},
                                {27.613, 27.613, 27.613, 27.613, 27.613, 27.613, 27.613, 27.613},
                                {25.829, 25.829, 25.829, 25.829, 25.829, 25.829, 25.829, 25.829},
                                {24.038, 24.038, 24.038, 24.038, 24.038, 24.038, 24.038, 24.038},
                                {22.239, 22.239, 22.239, 22.239, 22.239, 22.239, 22.239, 22.239},
                                {20.434, 20.434, 20.434, 20.434, 20.434, 20.434, 20.434, 20.434},
                                {18.621, 18.621, 18.621, 18.621, 18.621, 18.621, 18.621, 18.621},
                                {16.804, 16.804, 16.804, 16.804, 16.804, 16.804, 16.804, 16.804},
                                {14.983, 14.983, 14.983, 14.983, 14.983, 14.983, 14.983, 14.983},
                                {13.16, 13.16, 13.16, 13.16, 13.16, 13.16, 13.16, 13.16},
                                {11.337, 11.337, 11.337, 11.337, 11.337, 11.337, 11.337, 11.337},
                                {9.518, 9.518, 9.518, 9.518, 9.518, 9.518, 9.518, 9.518},
                                {7.706, 7.706, 7.706, 7.706, 7.706, 7.706, 7.706, 7.706},
                                {5.902, 5.902, 5.902, 5.902, 5.902, 5.902, 5.902, 5.902},
                        },
                },
                {
                        {//100 MHz, 10%, Cold Sea, Fig5
                                {97.935, 102.299, 105.725, 106.905, 107.062, 107.074, 107.074, 107.074},
                                {88.379, 92.582, 97.059, 100.203, 101.064, 101.156, 101.161, 101.161},
                                {82.648, 86.625, 91.195, 95.477, 97.468, 97.78, 97.802, 97.803},
                                {78.482, 82.298, 86.789, 91.549, 94.67, 95.384, 95.445, 95.448},
                                {75.167, 78.869, 83.265, 88.202, 92.211, 93.491, 93.625, 93.632},
                                {72.384, 76.007, 80.323, 85.317, 89.939, 91.887, 92.137, 92.15},
                                {69.962, 73.534, 77.789, 82.796, 87.816, 90.457, 90.874, 90.899},
                                {67.801, 71.343, 75.557, 80.563, 85.836, 89.133, 89.771, 89.815},
                                {65.838, 69.367, 73.556, 78.56, 83.994, 87.876, 88.786, 88.856},
                                {64.03, 67.56, 71.738, 76.744, 82.282, 86.664, 87.892, 87.996},
                                {62.344, 65.888, 70.067, 75.082, 80.692, 85.488, 87.067, 87.217},
                                {60.761, 64.327, 68.518, 73.547, 79.21, 84.343, 86.295, 86.502},
                                {59.263, 62.86, 67.072, 72.122, 77.825, 83.229, 85.565, 85.842},
                                {57.838, 61.473, 65.714, 70.789, 76.526, 82.148, 84.867, 85.228},
                                {56.477, 60.155, 64.432, 69.536, 75.304, 81.1, 84.194, 84.653},
                                {55.171, 58.897, 63.218, 68.354, 74.151, 80.086, 83.54, 84.111},
                                {53.916, 57.694, 62.062, 67.234, 73.058, 79.106, 82.902, 83.599},
                                {52.706, 56.539, 60.96, 66.169, 72.02, 78.161, 82.275, 83.112},
                                {51.537, 55.428, 59.906, 65.153, 71.031, 77.247, 81.657, 82.647},
                                {50.408, 54.357, 58.896, 64.182, 70.086, 76.365, 81.047, 82.201},
                                {45.287, 49.527, 54.407, 59.876, 65.898, 72.379, 78.087, 80.192},
                                {40.877, 45.505, 50.635, 56.237, 62.347, 68.93, 75.252, 78.409},
                                {37.447, 42.233, 47.437, 53.094, 59.24, 65.871, 72.557, 76.734},
                                {34.847, 39.579, 44.736, 50.353, 56.47, 63.104, 70.011, 75.103},
                                {32.884, 37.442, 42.445, 47.936, 53.962, 60.559, 67.6, 73.478},
                                {31.428, 35.723, 40.494, 45.791, 51.673, 58.194, 65.31, 71.839},
                                {30.34, 34.325, 38.814, 43.872, 49.57, 55.983, 63.128, 70.181},
                                {29.502, 33.159, 37.346, 42.139, 47.628, 53.907, 61.045, 68.504},
                                {28.922, 32.231, 36.092, 40.595, 45.849, 51.974, 59.085, 66.866},
                                {28.226, 31.263, 34.857, 39.113, 44.151, 50.113, 57.148, 65.124},
                                {27.678, 30.443, 33.769, 37.771, 42.584, 48.374, 55.323, 63.439},
                                {27.147, 29.67, 32.752, 36.517, 41.115, 46.729, 53.576, 61.769},
                                {26.615, 28.927, 31.791, 35.338, 39.73, 45.171, 51.902, 60.122},
                                {26.074, 28.202, 30.871, 34.22, 38.421, 43.691, 50.297, 58.503},
                                {25.518, 27.487, 29.985, 33.155, 37.178, 42.283, 48.758, 56.918},
                                {24.947, 26.778, 29.125, 32.134, 35.992, 40.939, 47.279, 55.369},
                                {23.76, 25.367, 27.463, 30.198, 33.765, 38.419, 44.488, 52.386},
                                {22.525, 23.961, 25.859, 28.371, 31.691, 36.082, 41.889, 49.557},
                                {21.254, 22.556, 24.298, 26.625, 29.736, 33.894, 39.451, 46.875},
                                {19.959, 21.155, 22.768, 24.942, 27.873, 31.824, 37.15, 44.327},
                                {18.649, 19.759, 21.266, 23.312, 26.087, 29.854, 34.966, 41.901},
                                {17.318, 18.362, 19.785, 21.723, 24.364, 27.962, 32.865, 39.544},
                                {15.945, 16.948, 18.313, 20.17, 22.696, 26.131, 30.804, 37.16},
                                {14.602, 15.566, 16.876, 18.659, 21.084, 24.383, 28.87, 34.974},
                                {13.282, 14.208, 15.468, 17.184, 19.52, 22.7, 27.029, 32.922},
                                {11.974, 12.881, 14.113, 15.788, 18.063, 21.155, 25.356, 31.065},
                                {8.807, 9.632, 10.757, 12.292, 14.385, 17.239, 21.132, 26.441},
                                {5.767, 6.546, 7.606, 9.05, 11.018, 13.698, 17.35, 22.324},
                                {2.876, 3.622, 4.635, 6.012, 7.882, 10.422, 13.874, 18.564},
                                {0.136, 0.859, 1.838, 3.162, 4.955, 7.382, 10.667, 15.113},
                                {-2.463, -1.756, -0.803, 0.482, 2.213, 4.546, 7.69, 11.929},
                                {-4.935, -4.239, -3.306, -2.053, -0.373, 1.882, 4.908, 8.968},
                                {-7.301, -6.613, -5.695, -4.468, -2.829, -0.639, 2.286, 6.192},
                                {-9.58, -8.899, -7.992, -6.785, -5.181, -3.047, -0.209, 3.565},
                                {-11.793, -11.115, -10.217, -9.028, -7.453, -5.367, -2.604, 1.056},
                                {-13.957, -13.282, -12.391, -11.216, -9.666, -7.621, -4.923, -1.363},
                                {-16.086, -15.413, -14.529, -13.366, -11.837, -9.827, -7.185, -3.713},
                                {-18.194, -17.522, -16.642, -15.49, -13.979, -12.001, -9.409, -6.013},
                                {-20.289, -19.619, -18.743, -17.599, -16.104, -14.153, -11.605, -8.277},
                                {-22.38, -21.709, -20.837, -19.7, -18.22, -16.293, -13.783, -10.515},
                                {-24.47, -23.8, -22.93, -21.8, -20.332, -18.426, -15.951, -12.736},
                                {-26.562, -25.892, -25.024, -23.9, -22.443, -20.556, -18.112, -14.945},
                                {-28.657, -27.987, -27.121, -26.002, -24.555, -22.685, -20.268, -17.144},
                                {-30.755, -30.085, -29.22, -28.106, -26.668, -24.813, -22.421, -19.335},
                                {-32.853, -32.183, -31.32, -30.209, -28.779, -26.938, -24.568, -21.517},
                                {-34.949, -34.279, -33.417, -32.31, -30.887, -29.059, -26.709, -23.689},
                                {-37.04, -36.369, -35.508, -34.404, -32.988, -31.171, -28.839, -25.848},
                                {-39.12, -38.449, -37.589, -36.488, -35.078, -33.271, -30.956, -27.991},
                                {-41.186, -40.515, -39.656, -38.557, -37.152, -35.355, -33.055, -30.114},
                                {-43.233, -42.561, -41.703, -40.607, -39.207, -37.418, -35.132, -32.213},
                                {-45.256, -44.584, -43.726, -42.632, -41.236, -39.455, -37.183, -34.283},
                                {-47.25, -46.577, -45.72, -44.628, -43.236, -41.462, -39.202, -36.321},
                                {-49.209, -48.537, -47.68, -46.59, -45.202, -43.434, -41.185, -38.321},
                                {-51.131, -50.458, -49.602, -48.513, -47.128, -45.367, -43.128, -40.28},
                                {-53.009, -52.336, -51.48, -50.393, -49.012, -47.256, -45.026, -42.193},
                                {-54.841, -54.168, -53.312, -52.227, -50.848, -49.098, -46.877, -44.056},
                                {-56.623, -55.949, -55.094, -54.009, -52.634, -50.889, -48.675, -45.868},
                                {-58.351, -57.677, -56.822, -55.738, -54.365, -52.625, -50.419, -47.623},
                        },
                        {//600 MHz, 10%, Cold Sea, Fig13
                                {107.039, 107.069, 107.073, 107.074, 107.074, 107.074, 107.074, 107.074},
                                {100.292, 101.061, 101.149, 101.161, 101.161, 101.161, 101.161, 101.161},
                                {94.553, 97.257, 97.742, 97.802, 97.803, 97.803, 97.803, 97.803},
                                {89.791, 93.886, 95.258, 95.445, 95.448, 95.448, 95.448, 95.448},
                                {86.015, 90.642, 93.184, 93.623, 93.632, 93.632, 93.632, 93.632},
                                {82.917, 87.632, 91.281, 92.128, 92.15, 92.15, 92.15, 92.15},
                                {80.294, 85.029, 89.439, 90.847, 90.9, 90.9, 90.9, 90.9},
                                {78.02, 82.718, 87.627, 89.707, 89.814, 89.816, 89.816, 89.816},
                                {76.012, 80.636, 85.85, 88.657, 88.855, 88.859, 88.859, 88.859},
                                {74.214, 78.746, 84.128, 87.654, 87.993, 88.001, 88.001, 88.001},
                                {72.583, 77.018, 82.475, 86.665, 87.209, 87.223, 87.224, 87.224},
                                {71.089, 75.427, 80.898, 85.662, 86.487, 86.512, 86.512, 86.512},
                                {69.709, 73.949, 79.4, 84.627, 85.815, 85.856, 85.857, 85.857},
                                {68.423, 72.567, 77.976, 83.551, 85.182, 85.247, 85.248, 85.248},
                                {67.215, 71.265, 76.622, 82.435, 84.579, 84.678, 84.68, 84.68},
                                {66.073, 70.032, 75.331, 81.285, 83.996, 84.144, 84.148, 84.148},
                                {64.986, 68.857, 74.098, 80.114, 83.426, 83.641, 83.646, 83.646},
                                {63.944, 67.731, 72.916, 78.933, 82.86, 83.165, 83.172, 83.172},
                                {62.94, 66.647, 71.781, 77.756, 82.29, 82.712, 82.723, 82.723},
                                {61.967, 65.599, 70.687, 76.592, 81.708, 82.28, 82.295, 82.295},
                                {57.403, 60.761, 65.71, 71.179, 78.434, 80.348, 80.422, 80.423},
                                {53.168, 56.424, 61.381, 66.626, 74.485, 78.604, 78.87, 78.876},
                                {49.251, 52.54, 57.704, 62.924, 70.396, 76.787, 77.533, 77.554},
                                {46.045, 49.79, 54.788, 59.95, 66.718, 74.649, 76.336, 76.396},
                                {44.05, 47.946, 52.527, 57.531, 63.644, 72.1, 75.216, 75.364},
                                {42.608, 46.414, 50.657, 55.485, 61.111, 69.294, 74.1, 74.43},
                                {41.319, 44.965, 48.99, 53.672, 58.969, 66.486, 72.907, 73.572},
                                {40.039, 43.563, 47.438, 52.004, 57.084, 63.87, 71.553, 72.771},
                                {38.777, 42.205, 45.966, 50.434, 55.364, 61.522, 69.976, 72.011},
                                {37.545, 40.891, 44.56, 48.938, 53.757, 59.44, 68.166, 71.271},
                                {36.349, 39.622, 43.211, 47.504, 52.231, 57.584, 66.171, 70.529},
                                {35.193, 38.398, 41.912, 46.124, 50.769, 55.901, 64.071, 69.76},
                                {34.074, 37.216, 40.66, 44.792, 49.36, 54.345, 61.949, 68.932},
                                {32.993, 36.073, 39.45, 43.505, 47.998, 52.879, 59.875, 68.015},
                                {31.945, 34.967, 38.279, 42.258, 46.679, 51.479, 57.907, 66.982},
                                {30.93, 33.894, 37.144, 41.049, 45.399, 50.131, 56.089, 65.812},
                                {28.985, 31.839, 34.969, 38.731, 42.943, 47.553, 52.931, 63.041},
                                {27.14, 29.89, 32.906, 36.531, 40.612, 45.106, 50.236, 59.747},
                                {25.381, 28.032, 30.939, 34.435, 38.389, 42.77, 47.779, 56.115},
                                {23.694, 26.252, 29.056, 32.428, 36.262, 40.532, 45.455, 52.634},
                                {22.07, 24.539, 27.246, 30.502, 34.219, 38.383, 43.224, 49.749},
                                {20.501, 22.886, 25.501, 28.645, 32.253, 36.313, 41.073, 47.346},
                                {18.98, 21.285, 23.812, 26.852, 30.356, 34.315, 38.993, 45.176},
                                {17.501, 19.731, 22.176, 25.116, 28.521, 32.384, 36.979, 43.116},
                                {16.061, 18.219, 20.586, 23.432, 26.743, 30.514, 35.025, 41.123},
                                {14.655, 16.746, 19.038, 21.795, 25.018, 28.7, 33.13, 39.184},
                                {11.273, 13.21, 15.334, 17.888, 20.907, 24.385, 28.617, 34.541},
                                {8.053, 9.855, 11.831, 14.208, 17.048, 20.345, 24.392, 30.161},
                                {4.971, 6.654, 8.501, 10.721, 13.404, 16.539, 20.416, 26.019},
                                {2.009, 3.589, 5.321, 7.403, 9.947, 12.937, 16.658, 22.094},
                                {-0.844, 0.643, 2.274, 4.234, 6.654, 9.516, 13.093, 18.365},
                                {-3.599, -2.194, -0.653, 1.199, 3.509, 6.255, 9.701, 14.813},
                                {-6.261, -4.93, -3.47, -1.714, 0.497, 3.139, 6.465, 11.425},
                                {-8.839, -7.573, -6.185, -4.516, -2.394, 0.154, 3.371, 8.185},
                                {-11.336, -10.13, -8.807, -7.216, -5.173, -2.71, 0.407, 5.082},
                                {-13.757, -12.604, -11.34, -9.819, -7.849, -5.462, -2.438, 2.106},
                                {-16.105, -15.001, -13.79, -12.333, -10.428, -8.112, -5.173, -0.752},
                                {-18.385, -17.325, -16.162, -14.763, -12.918, -10.666, -7.805, -3.5},
                                {-20.599, -19.578, -18.46, -17.114, -15.323, -13.13, -10.341, -6.146},
                                {-22.749, -21.766, -20.687, -19.39, -17.649, -15.51, -12.787, -8.696},
                                {-24.839, -23.889, -22.847, -21.595, -19.899, -17.81, -15.149, -11.155},
                                {-26.87, -25.951, -24.944, -23.732, -22.079, -20.035, -17.432, -13.53},
                                {-28.845, -27.955, -26.979, -25.805, -24.19, -22.189, -19.639, -15.824},
                                {-30.766, -29.902, -28.955, -27.816, -26.237, -24.275, -21.776, -18.042},
                                {-32.634, -31.795, -30.875, -29.769, -28.223, -26.297, -23.844, -20.188},
                                {-34.452, -33.636, -32.741, -31.664, -30.15, -28.258, -25.848, -22.265},
                                {-36.221, -35.426, -34.554, -33.506, -32.02, -30.16, -27.791, -24.277},
                                {-37.943, -37.168, -36.318, -35.295, -33.836, -32.006, -29.675, -26.226},
                                {-39.619, -38.863, -38.033, -37.035, -35.601, -33.798, -31.503, -28.116},
                                {-41.252, -40.512, -39.701, -38.726, -37.316, -35.539, -33.277, -29.949},
                                {-42.835, -42.115, -41.325, -40.371, -38.983, -37.23, -35, -31.727},
                                {-44.378, -43.676, -42.905, -41.971, -40.603, -38.874, -36.673, -33.454},
                                {-45.88, -45.196, -44.442, -43.527, -42.179, -40.471, -38.299, -35.13},
                                {-47.344, -46.677, -45.94, -45.042, -43.713, -42.025, -39.879, -36.758},
                                {-48.771, -48.119, -47.397, -46.517, -45.205, -43.536, -41.416, -38.34},
                                {-50.162, -49.523, -48.817, -47.953, -46.657, -45.006, -42.91, -39.877},
                                {-51.517, -50.892, -50.2, -49.35, -48.07, -46.437, -44.363, -41.372},
                                {-52.838, -52.226, -51.547, -50.712, -49.446, -47.829, -45.776, -42.825},
                        },
                        {//2000 MHz, 10%, Cold Sea, Fig21
                                {107.067, 107.073, 107.074, 107.074, 107.074, 107.074, 107.074, 107.074},
                                {101.135, 101.155, 101.16, 101.161, 101.161, 101.161, 101.161, 101.161},
                                {97.743, 97.787, 97.799, 97.802, 97.803, 97.803, 97.803, 97.803},
                                {95.341, 95.417, 95.441, 95.447, 95.448, 95.448, 95.448, 95.448},
                                {93.465, 93.58, 93.619, 93.63, 93.632, 93.632, 93.632, 93.632},
                                {91.91, 92.071, 92.13, 92.147, 92.15, 92.15, 92.15, 92.15},
                                {90.571, 90.786, 90.869, 90.894, 90.9, 90.9, 90.9, 90.9},
                                {89.387, 89.661, 89.771, 89.806, 89.815, 89.816, 89.816, 89.816},
                                {88.316, 88.655, 88.797, 88.845, 88.857, 88.859, 88.859, 88.859},
                                {87.334, 87.741, 87.919, 87.981, 87.998, 88.001, 88.001, 88.001},
                                {86.419, 86.9, 87.117, 87.197, 87.219, 87.223, 87.224, 87.224},
                                {85.56, 86.117, 86.378, 86.478, 86.506, 86.512, 86.512, 86.512},
                                {84.746, 85.383, 85.691, 85.812, 85.849, 85.856, 85.857, 85.857},
                                {83.969, 84.688, 85.046, 85.192, 85.238, 85.247, 85.248, 85.248},
                                {83.224, 84.027, 84.438, 84.611, 84.667, 84.679, 84.68, 84.68},
                                {82.504, 83.393, 83.86, 84.063, 84.131, 84.146, 84.148, 84.148},
                                {81.808, 82.783, 83.309, 83.544, 83.625, 83.644, 83.646, 83.646},
                                {81.132, 82.193, 82.781, 83.05, 83.146, 83.169, 83.172, 83.172},
                                {80.473, 81.622, 82.272, 82.578, 82.691, 82.719, 82.723, 82.723},
                                {79.83, 81.065, 81.781, 82.126, 82.257, 82.291, 82.295, 82.295},
                                {76.8, 78.452, 79.514, 80.089, 80.336, 80.41, 80.423, 80.423},
                                {74.009, 76.03, 77.448, 78.298, 78.707, 78.846, 78.874, 78.876},
                                {71.403, 73.735, 75.493, 76.644, 77.259, 77.495, 77.549, 77.554},
                                {68.957, 71.538, 73.603, 75.065, 75.924, 76.292, 76.386, 76.397},
                                {66.655, 69.428, 71.758, 73.523, 74.654, 75.191, 75.346, 75.367},
                                {64.484, 67.402, 69.952, 71.998, 73.417, 74.161, 74.401, 74.436},
                                {62.434, 65.458, 68.184, 70.481, 72.19, 73.175, 73.528, 73.587},
                                {60.496, 63.593, 66.457, 68.97, 70.96, 72.213, 72.711, 72.805},
                                {58.659, 61.806, 64.774, 67.466, 69.718, 71.257, 71.935, 72.078},
                                {56.917, 60.093, 63.137, 65.975, 68.463, 70.296, 71.188, 71.398},
                                {55.261, 58.452, 61.549, 64.502, 67.195, 69.32, 70.458, 70.756},
                                {53.687, 56.88, 60.012, 63.053, 65.919, 68.322, 69.735, 70.148},
                                {52.188, 55.376, 58.528, 61.632, 64.64, 67.302, 69.01, 69.565},
                                {50.761, 53.936, 57.096, 60.245, 63.366, 66.26, 68.275, 69.003},
                                {49.403, 52.559, 55.718, 58.895, 62.104, 65.2, 67.524, 68.457},
                                {48.109, 51.243, 54.393, 57.586, 60.86, 64.127, 66.753, 67.922},
                                {45.705, 48.788, 51.905, 55.1, 58.45, 61.972, 65.147, 66.863},
                                {43.527, 46.552, 49.624, 52.794, 56.167, 59.846, 63.465, 65.793},
                                {41.548, 44.513, 47.532, 50.662, 54.025, 57.786, 61.734, 64.682},
                                {39.736, 42.64, 45.603, 48.684, 52.017, 55.81, 59.986, 63.511},
                                {38.055, 40.9, 43.805, 46.834, 50.123, 53.916, 58.238, 62.269},
                                {36.47, 39.258, 42.107, 45.081, 48.32, 52.091, 56.499, 60.949},
                                {34.952, 37.684, 40.477, 43.396, 46.582, 50.317, 54.769, 59.55},
                                {33.476, 36.154, 38.892, 41.756, 44.887, 48.578, 53.042, 58.072},
                                {32.024, 34.65, 37.335, 40.145, 43.219, 46.861, 51.315, 56.52},
                                {30.586, 33.161, 35.796, 38.553, 41.571, 45.159, 49.586, 54.905},
                                {27.033, 29.49, 32.003, 34.633, 37.513, 40.96, 45.278, 50.686},
                                {23.572, 25.919, 28.32, 30.83, 33.579, 36.886, 41.065, 46.402},
                                {20.265, 22.512, 24.808, 27.209, 29.836, 33.007, 37.042, 42.248},
                                {17.155, 19.309, 21.509, 23.809, 26.323, 29.368, 33.263, 38.323},
                                {14.251, 16.319, 18.431, 20.636, 23.046, 25.975, 29.739, 34.654},
                                {11.535, 13.524, 15.554, 17.673, 19.986, 22.807, 26.448, 31.226},
                                {8.983, 10.897, 12.851, 14.89, 17.113, 19.833, 23.36, 28.008},
                                {6.564, 8.41, 10.292, 12.256, 14.396, 17.021, 20.442, 24.968},
                                {4.255, 6.035, 7.851, 9.745, 11.806, 14.343, 17.663, 22.075},
                                {2.034, 3.753, 5.507, 7.334, 9.321, 11.775, 15.001, 19.305},
                                {-0.114, 1.548, 3.242, 5.007, 6.925, 9.3, 12.436, 16.639},
                                {-2.201, -0.594, 1.044, 2.75, 4.602, 6.902, 9.954, 14.06},
                                {-4.237, -2.681, -1.096, 0.553, 2.342, 4.572, 7.544, 11.558},
                                {-6.227, -4.722, -3.187, -1.591, 0.139, 2.301, 5.196, 9.123},
                                {-8.179, -6.72, -5.234, -3.689, -2.016, 0.083, 2.904, 6.747},
                                {-10.094, -8.68, -7.24, -5.744, -4.126, -2.089, 0.663, 4.426},
                                {-11.977, -10.606, -9.21, -7.761, -6.194, -4.216, -1.532, 2.154},
                                {-13.829, -12.5, -11.146, -9.742, -8.225, -6.304, -3.684, -0.072},
                                {-15.652, -14.363, -13.051, -11.689, -10.221, -8.354, -5.796, -2.254},
                                {-17.448, -16.197, -14.924, -13.604, -12.182, -10.368, -7.869, -4.396},
                                {-19.218, -18.004, -16.769, -15.488, -14.111, -12.347, -9.907, -6.5},
                                {-20.962, -19.783, -18.584, -17.343, -16.009, -14.294, -11.909, -8.565},
                                {-22.68, -21.536, -20.372, -19.168, -17.875, -16.208, -13.877, -10.595},
                                {-24.372, -23.262, -22.132, -20.964, -19.712, -18.09, -15.811, -12.589},
                                {-26.04, -24.961, -23.865, -22.732, -21.519, -19.941, -17.713, -14.549},
                                {-27.682, -26.634, -25.57, -24.471, -23.296, -21.761, -19.582, -16.474},
                                {-29.298, -28.281, -27.248, -26.182, -25.044, -23.551, -21.419, -18.366},
                                {-30.889, -29.901, -28.898, -27.864, -26.761, -25.309, -23.224, -20.223},
                                {-32.453, -31.494, -30.521, -29.517, -28.45, -27.037, -24.996, -22.047},
                                {-33.991, -33.059, -32.115, -31.141, -30.108, -28.733, -26.737, -23.838},
                                {-35.501, -34.597, -33.68, -32.736, -31.736, -30.399, -28.445, -25.595},
                                {-36.985, -36.107, -35.217, -34.302, -33.333, -32.033, -30.12, -27.318},
                        },
                },
                {
                        {//100 MHz, 50%, Cold Sea, Fig4
                                {97.931, 102.263, 105.611, 106.74, 106.889, 106.9, 106.9, 106.9},
                                {88.379, 92.572, 96.98, 99.991, 100.79, 100.874, 100.879, 100.879},
                                {82.648, 86.625, 91.136, 95.249, 97.062, 97.338, 97.357, 97.358},
                                {78.482, 82.298, 86.746, 91.348, 94.193, 94.805, 94.856, 94.859},
                                {75.167, 78.869, 83.234, 88.032, 91.719, 92.805, 92.915, 92.92},
                                {72.384, 76.007, 80.3, 85.168, 89.471, 91.124, 91.325, 91.337},
                                {69.962, 73.534, 77.773, 82.658, 87.384, 89.645, 89.977, 89.997},
                                {67.801, 71.343, 75.545, 80.429, 85.436, 88.296, 88.803, 88.837},
                                {65.838, 69.367, 73.547, 78.425, 83.619, 87.035, 87.759, 87.813},
                                {64.03, 67.56, 71.73, 76.604, 81.923, 85.833, 86.816, 86.897},
                                {62.344, 65.888, 70.06, 74.933, 80.337, 84.675, 85.951, 86.067},
                                {60.761, 64.327, 68.511, 73.389, 78.853, 83.551, 85.149, 85.309},
                                {59.263, 62.86, 67.063, 71.952, 77.46, 82.457, 84.395, 84.61},
                                {57.838, 61.473, 65.702, 70.606, 76.15, 81.391, 83.679, 83.962},
                                {56.477, 60.155, 64.416, 69.339, 74.912, 80.354, 82.994, 83.357},
                                {55.171, 58.897, 63.196, 68.142, 73.74, 79.345, 82.332, 82.789},
                                {53.916, 57.694, 62.033, 67.005, 72.628, 78.365, 81.689, 82.253},
                                {52.706, 56.539, 60.921, 65.923, 71.567, 77.413, 81.06, 81.745},
                                {51.537, 55.428, 59.856, 64.888, 70.555, 76.489, 80.44, 81.262},
                                {50.408, 54.357, 58.832, 63.897, 69.585, 75.593, 79.829, 80.801},
                                {45.287, 49.527, 54.238, 59.471, 65.263, 71.492, 76.841, 78.73},
                                {40.877, 45.365, 50.281, 55.664, 61.548, 67.887, 73.918, 76.896},
                                {37.108, 41.759, 46.81, 52.295, 58.246, 64.653, 71.086, 75.152},
                                {33.912, 38.619, 43.719, 49.245, 55.23, 61.683, 68.371, 73.415},
                                {31.2, 35.863, 40.93, 46.436, 52.417, 58.9, 65.767, 71.643},
                                {28.941, 33.46, 38.41, 43.832, 49.77, 56.264, 63.272, 69.838},
                                {27.042, 31.343, 36.111, 41.395, 47.252, 53.737, 60.864, 68},
                                {25.433, 29.465, 34.002, 39.105, 44.846, 51.301, 58.526, 66.139},
                                {24.052, 27.784, 32.056, 36.944, 42.54, 48.941, 56.246, 64.264},
                                {22.844, 26.263, 30.251, 34.902, 40.327, 46.652, 54.017, 62.381},
                                {21.767, 24.873, 28.57, 32.968, 38.203, 44.431, 51.834, 60.496},
                                {20.784, 23.588, 26.996, 31.135, 36.165, 42.275, 49.695, 58.614},
                                {19.869, 22.389, 25.515, 29.395, 34.211, 40.186, 47.6, 56.739},
                                {19.003, 21.258, 24.117, 27.742, 32.338, 38.164, 45.55, 54.875},
                                {18.172, 20.185, 22.792, 26.169, 30.543, 36.209, 43.547, 53.027},
                                {17.365, 19.158, 21.531, 24.671, 28.825, 34.322, 41.594, 51.2},
                                {15.8, 17.22, 19.178, 21.879, 25.606, 30.748, 37.843, 47.624},
                                {14.28, 15.402, 17.014, 19.33, 22.658, 27.439, 34.308, 44.175},
                                {12.791, 13.681, 15.009, 16.994, 19.958, 24.386, 30.999, 40.875},
                                {11.332, 12.042, 13.142, 14.846, 17.486, 21.577, 27.915, 37.736},
                                {9.903, 10.476, 11.394, 12.865, 15.222, 19.001, 25.057, 34.763},
                                {8.508, 8.978, 9.753, 11.033, 13.149, 16.644, 22.418, 31.957},
                                {7.15, 7.542, 8.207, 9.335, 11.247, 14.49, 19.989, 29.316},
                                {5.831, 6.167, 6.748, 7.755, 9.5, 12.522, 17.76, 26.833},
                                {4.553, 4.847, 5.366, 6.28, 7.89, 10.724, 15.714, 24.502},
                                {3.315, 3.581, 4.055, 4.898, 6.4, 9.075, 13.837, 22.315},
                                {0.387, 0.618, 1.033, 1.775, 3.105, 5.488, 9.76, 17.415},
                                {-2.335, -2.106, -1.704, -0.995, 0.256, 2.462, 6.351, 13.209},
                                {-4.898, -4.656, -4.24, -3.527, -2.305, -0.208, 3.388, 9.555},
                                {-7.344, -7.079, -6.639, -5.906, -4.688, -2.661, 0.711, 6.321},
                                {-9.701, -9.411, -8.943, -8.186, -6.961, -4.981, -1.779, 3.4},
                                {-11.989, -11.675, -11.18, -10.399, -9.166, -7.218, -4.143, 0.712},
                                {-14.22, -13.885, -13.367, -12.565, -11.322, -9.398, -6.419, -1.806},
                                {-16.403, -16.05, -15.512, -14.692, -13.442, -11.535, -8.628, -4.194},
                                {-18.544, -18.176, -17.623, -16.788, -15.531, -13.637, -10.783, -6.484},
                                {-20.649, -20.269, -19.702, -18.856, -17.594, -15.709, -12.896, -8.698},
                                {-22.724, -22.334, -21.757, -20.902, -19.634, -17.757, -14.975, -10.853},
                                {-24.774, -24.377, -23.791, -22.928, -21.657, -19.785, -17.027, -12.964},
                                {-26.807, -26.403, -25.811, -24.942, -23.668, -21.799, -19.06, -15.042},
                                {-28.828, -28.419, -27.821, -26.947, -25.671, -23.805, -21.08, -17.098},
                                {-30.844, -30.43, -29.828, -28.95, -27.671, -25.808, -23.094, -19.14},
                                {-32.859, -32.442, -31.836, -30.955, -29.674, -27.813, -25.108, -21.175},
                                {-34.879, -34.459, -33.85, -32.966, -31.684, -29.824, -27.126, -23.211},
                                {-36.907, -36.485, -35.873, -34.988, -33.704, -31.846, -29.153, -25.253},
                                {-38.947, -38.523, -37.909, -37.021, -35.737, -33.879, -31.191, -27.302},
                                {-40.999, -40.573, -39.958, -39.069, -37.784, -35.926, -33.242, -29.363},
                                {-43.065, -42.638, -42.021, -41.131, -39.845, -37.988, -35.307, -31.435},
                                {-45.143, -44.715, -44.097, -43.206, -41.919, -40.063, -37.384, -33.519},
                                {-47.233, -46.804, -46.185, -45.292, -44.005, -42.149, -39.472, -35.613},
                                {-49.33, -48.9, -48.281, -47.387, -46.1, -44.244, -41.569, -37.714},
                                {-51.432, -51.002, -50.381, -49.487, -48.199, -46.344, -43.67, -39.819},
                                {-53.534, -53.103, -52.482, -51.587, -50.299, -48.443, -45.771, -41.923},
                                {-55.63, -55.198, -54.577, -53.682, -52.393, -50.538, -47.867, -44.021},
                                {-57.714, -57.282, -56.66, -55.764, -54.475, -52.62, -49.95, -46.107},
                                {-59.779, -59.347, -58.724, -57.829, -56.539, -54.685, -52.015, -48.174},
                                {-61.819, -61.386, -60.764, -59.868, -58.578, -56.723, -54.055, -50.215},
                                {-63.827, -63.393, -62.77, -61.874, -60.585, -58.73, -56.062, -52.224},
                                {-65.794, -65.36, -64.737, -63.841, -62.551, -60.696, -58.029, -54.192},
                        },
                        {//600 MHz, 50%, Cold Sea, Fig12
                                {106.891, 106.893, 106.9, 106.9, 106.9, 106.9, 106.9, 106.9},
                                {100.292, 100.769, 100.875, 100.878, 100.879, 100.879, 100.879, 100.879},
                                {94.553, 96.847, 97.317, 97.352, 97.357, 97.358, 97.358, 97.358},
                                {89.791, 93.521, 94.673, 94.842, 94.858, 94.859, 94.859, 94.859},
                                {86.015, 90.437, 92.41, 92.879, 92.918, 92.92, 92.921, 92.921},
                                {82.917, 87.598, 90.371, 91.252, 91.332, 91.337, 91.337, 91.337},
                                {80.294, 85.029, 88.492, 89.844, 89.988, 89.997, 89.998, 89.998},
                                {78.02, 82.718, 86.719, 88.58, 88.82, 88.837, 88.838, 88.838},
                                {76.012, 80.636, 85.027, 87.41, 87.784, 87.813, 87.815, 87.815},
                                {74.214, 78.746, 83.407, 86.299, 86.85, 86.897, 86.9, 86.9},
                                {72.583, 77.018, 81.857, 85.221, 85.995, 86.067, 86.072, 86.072},
                                {71.089, 75.427, 80.375, 84.158, 85.203, 85.308, 85.316, 85.316},
                                {69.709, 73.949, 78.959, 83.097, 84.459, 84.609, 84.62, 84.621},
                                {68.423, 72.567, 77.605, 82.033, 83.753, 83.96, 83.976, 83.977},
                                {67.215, 71.265, 76.309, 80.963, 83.074, 83.354, 83.376, 83.378},
                                {66.073, 70.032, 75.067, 79.887, 82.415, 82.784, 82.815, 82.817},
                                {64.986, 68.857, 73.873, 78.808, 81.768, 82.246, 82.288, 82.291},
                                {63.944, 67.731, 72.722, 77.731, 81.126, 81.734, 81.79, 81.794},
                                {62.94, 66.647, 71.611, 76.659, 80.486, 81.246, 81.319, 81.325},
                                {61.967, 65.599, 70.535, 75.595, 79.841, 80.778, 80.872, 80.879},
                                {57.403, 60.761, 65.559, 70.504, 76.457, 78.641, 78.916, 78.939},
                                {53.168, 56.424, 61.029, 65.866, 72.741, 76.642, 77.29, 77.352},
                                {49.251, 52.512, 56.763, 61.612, 68.827, 74.568, 75.867, 76.006},
                                {45.145, 48.561, 52.694, 57.611, 64.893, 72.283, 74.553, 74.831},
                                {41.133, 44.695, 48.804, 53.765, 61.031, 69.736, 73.273, 73.782},
                                {37.432, 41.035, 45.098, 50.031, 57.26, 66.951, 71.959, 72.824},
                                {34.071, 37.617, 41.58, 46.417, 53.572, 63.988, 70.556, 71.928},
                                {30.982, 34.42, 38.249, 42.945, 49.962, 60.901, 69.017, 71.069},
                                {28.088, 31.41, 35.101, 39.635, 46.44, 57.72, 67.316, 70.224},
                                {25.337, 28.558, 32.127, 36.496, 43.024, 54.458, 65.443, 69.37},
                                {22.694, 25.842, 29.314, 33.53, 39.731, 51.126, 63.402, 68.487},
                                {20.136, 23.247, 26.653, 30.733, 36.578, 47.752, 61.197, 67.552},
                                {17.776, 20.812, 24.132, 28.099, 33.574, 44.37, 58.826, 66.548},
                                {15.529, 18.498, 21.744, 25.622, 30.726, 41.025, 56.275, 65.46},
                                {13.368, 16.289, 19.482, 23.298, 28.038, 37.755, 53.529, 64.276},
                                {11.297, 14.186, 17.345, 21.12, 25.511, 34.591, 50.585, 62.988},
                                {7.516, 10.362, 13.473, 17.192, 20.947, 28.669, 44.242, 60.068},
                                {4.498, 7.207, 10.21, 13.799, 17.023, 23.396, 37.694, 56.587},
                                {3.004, 4.751, 7.543, 10.88, 13.692, 18.855, 31.387, 52.282},
                                {1.541, 2.748, 5.301, 8.352, 10.866, 15.065, 25.617, 46.965},
                                {0.103, 0.98, 3.329, 6.138, 8.444, 11.962, 20.58, 40.928},
                                {-1.311, -0.648, 1.548, 4.173, 6.335, 9.415, 16.395, 34.736},
                                {-2.702, -2.137, -0.081, 2.407, 4.468, 7.279, 13.055, 28.85},
                                {-4.07, -3.557, -1.588, 0.797, 2.787, 5.435, 10.422, 23.581},
                                {-5.417, -4.923, -2.996, -0.693, 1.242, 3.79, 8.295, 19.132},
                                {-6.741, -6.205, -4.334, -2.097, -0.205, 2.277, 6.494, 15.557},
                                {-9.955, -9.274, -7.514, -5.411, -3.598, -1.212, 2.673, 9.498},
                                {-13.033, -12.311, -10.642, -8.646, -6.893, -4.566, -0.827, 5.292},
                                {-15.981, -15.355, -13.766, -11.867, -10.166, -7.887, -4.247, 1.553},
                                {-18.809, -18.342, -16.824, -15.009, -13.353, -11.115, -7.554, -1.927},
                                {-21.529, -21.201, -19.747, -18.009, -16.394, -14.194, -10.702, -5.214},
                                {-24.151, -23.902, -22.507, -20.841, -19.263, -17.097, -13.669, -8.307},
                                {-26.687, -26.453, -25.115, -23.515, -21.971, -19.838, -16.47, -11.223},
                                {-29.15, -28.884, -27.597, -26.06, -24.548, -22.445, -19.133, -13.995},
                                {-31.55, -31.224, -29.987, -28.508, -27.027, -24.952, -21.692, -16.656},
                                {-33.896, -33.501, -32.31, -30.886, -29.434, -27.386, -24.175, -19.236},
                                {-36.198, -35.736, -34.588, -33.216, -31.791, -29.768, -26.604, -21.756},
                                {-38.464, -37.943, -36.836, -35.514, -34.114, -32.115, -28.996, -24.234},
                                {-40.7, -40.132, -39.065, -37.789, -36.414, -34.438, -31.361, -26.682},
                                {-42.911, -42.31, -41.28, -40.05, -38.698, -36.744, -33.707, -29.106},
                                {-45.104, -44.482, -43.488, -42.3, -40.97, -39.037, -36.039, -31.513},
                                {-47.281, -46.65, -45.69, -44.543, -43.235, -41.322, -38.36, -33.907},
                                {-49.445, -48.815, -47.889, -46.781, -45.493, -43.599, -40.673, -36.288},
                                {-51.598, -50.978, -50.083, -49.013, -47.745, -45.869, -42.977, -38.659},
                                {-53.743, -53.137, -52.273, -51.239, -49.99, -48.132, -45.24, -41.018},
                                {-55.878, -55.291, -54.455, -53.457, -52.226, -50.278, -47.39, -43.364},
                                {-58.005, -57.436, -56.629, -55.665, -54.451, -52.412, -49.529, -45.695},
                                {-60.123, -59.57, -58.79, -57.859, -56.662, -54.536, -51.657, -48.009},
                                {-62.23, -61.689, -60.936, -60.035, -58.855, -56.649, -53.774, -50.144},
                                {-64.325, -63.788, -63.061, -62.191, -60.962, -58.748, -55.877, -52.258},
                                {-66.405, -65.863, -65.161, -64.321, -63.045, -60.834, -57.966, -54.355},
                                {-68.469, -67.91, -67.231, -66.42, -65.112, -62.901, -60.037, -56.435},
                                {-70.514, -69.923, -69.268, -68.485, -67.159, -64.95, -62.088, -58.493},
                                {-72.46, -71.891, -71.265, -70.509, -69.183, -66.975, -64.116, -60.528},
                                {-74.355, -73.816, -73.219, -72.489, -71.182, -68.975, -66.118, -62.537},
                                {-76.205, -75.693, -75.124, -74.42, -73.152, -70.946, -68.091, -64.515},
                                {-78.004, -77.519, -76.977, -76.298, -75.09, -72.885, -70.031, -66.461},
                                {-79.748, -79.29, -78.773, -78.119, -76.993, -74.789, -71.937, -68.371},
                        },
                        {//2000 MHz, 50%, Cold Sea, Fig20
                                {106.896, 106.899, 106.9, 106.9, 106.9, 106.9, 106.9, 106.9},
                                {100.851, 100.871, 100.878, 100.879, 100.879, 100.879, 100.879, 100.879},
                                {97.265, 97.327, 97.35, 97.357, 97.358, 97.358, 97.358, 97.358},
                                {94.647, 94.782, 94.837, 94.856, 94.859, 94.859, 94.859, 94.859},
                                {92.52, 92.764, 92.873, 92.914, 92.921, 92.921, 92.921, 92.921},
                                {90.664, 91.059, 91.247, 91.323, 91.337, 91.337, 91.337, 91.337},
                                {88.961, 89.548, 89.844, 89.971, 89.998, 89.998, 89.998, 89.998},
                                {87.338, 88.156, 88.593, 88.792, 88.837, 88.838, 88.838, 88.838},
                                {85.752, 86.835, 87.445, 87.74, 87.814, 87.815, 87.815, 87.815},
                                {84.173, 85.55, 86.367, 86.785, 86.897, 86.9, 86.9, 86.9},
                                {82.585, 84.277, 85.333, 85.903, 86.068, 86.072, 86.072, 86.072},
                                {80.98, 82.998, 84.323, 85.075, 85.309, 85.316, 85.316, 85.316},
                                {79.357, 81.704, 83.321, 84.288, 84.61, 84.621, 84.621, 84.621},
                                {77.715, 80.387, 82.314, 83.528, 83.961, 83.977, 83.977, 83.977},
                                {76.06, 79.044, 81.294, 82.786, 83.354, 83.378, 83.378, 83.378},
                                {74.396, 77.675, 80.254, 82.052, 82.782, 82.817, 82.818, 82.818},
                                {72.73, 76.281, 79.189, 81.318, 82.241, 82.291, 82.291, 82.291},
                                {71.067, 74.866, 78.097, 80.578, 81.726, 81.794, 81.795, 81.795},
                                {69.414, 73.435, 76.976, 79.826, 81.232, 81.324, 81.325, 81.325},
                                {67.774, 71.991, 75.827, 79.056, 80.756, 80.879, 80.879, 80.879},
                                {59.906, 64.756, 69.741, 74.834, 78.512, 78.937, 78.941, 78.941},
                                {52.745, 57.81, 63.403, 69.926, 76.198, 77.339, 77.358, 77.358},
                                {46.318, 51.376, 57.195, 64.535, 73.432, 75.954, 76.019, 76.019},
                                {40.545, 45.497, 51.325, 58.984, 69.941, 74.668, 74.858, 74.859},
                                {35.334, 40.138, 45.867, 53.53, 65.684, 73.342, 73.834, 73.836},
                                {30.599, 35.242, 40.824, 48.314, 60.852, 71.785, 72.915, 72.921},
                                {26.267, 30.751, 36.168, 43.398, 55.727, 69.755, 72.076, 72.093},
                                {22.28, 26.613, 31.866, 38.797, 50.561, 67.015, 71.29, 71.337},
                                {18.596, 22.79, 27.884, 34.504, 45.52, 63.458, 70.52, 70.642},
                                {15.185, 19.25, 24.195, 30.507, 40.703, 59.174, 69.707, 69.998},
                                {12.036, 15.98, 20.782, 26.792, 36.16, 54.405, 68.752, 69.398},
                                {9.151, 12.975, 17.634, 23.348, 31.914, 49.421, 67.5, 68.836},
                                {6.55, 10.245, 14.75, 20.166, 27.978, 44.44, 65.743, 68.307},
                                {4.259, 7.807, 12.133, 17.241, 24.356, 39.611, 63.276, 67.803},
                                {2.99, 5.676, 9.787, 14.571, 21.046, 35.026, 60.002, 67.315},
                                {2.327, 3.855, 7.708, 12.149, 18.044, 30.74, 55.998, 66.829},
                                {1.061, 2.131, 4.298, 8.02, 12.92, 23.174, 46.736, 65.729},
                                {-0.158, 0.716, 1.934, 4.745, 8.854, 16.999, 37.327, 63.948},
                                {-1.356, -0.628, 0.443, 2.249, 5.659, 12.134, 28.841, 60.302},
                                {-2.55, -1.932, -0.972, 0.686, 3.187, 8.377, 21.701, 53.843},
                                {-3.746, -3.213, -2.338, -0.794, 1.548, 5.475, 15.978, 45.422},
                                {-4.95, -4.482, -3.674, -2.219, -0.002, 3.287, 11.535, 36.725},
                                {-6.161, -5.745, -4.989, -3.604, -1.486, 1.637, 8.111, 28.811},
                                {-7.38, -7.004, -6.29, -4.962, -2.923, 0.066, 5.425, 22.093},
                                {-8.604, -8.261, -7.581, -6.297, -4.322, -1.442, 3.237, 16.618},
                                {-9.831, -9.515, -8.861, -7.614, -5.692, -2.901, 1.369, 12.25},
                                {-12.895, -12.626, -12.02, -10.839, -9.011, -6.382, -2.565, 4.72},
                                {-15.923, -15.685, -15.11, -13.963, -12.195, -9.668, -6.003, -0.199},
                                {-18.888, -18.67, -18.046, -16.825, -15.117, -12.673, -9.163, -3.994},
                                {-21.771, -21.567, -20.743, -19.548, -17.891, -15.512, -12.103, -7.239},
                                {-24.562, -24.306, -23.308, -22.137, -20.523, -18.199, -14.866, -10.17},
                                {-27.26, -26.753, -25.77, -24.619, -23.043, -20.768, -17.498, -12.911},
                                {-29.866, -29.131, -28.161, -27.03, -25.488, -23.258, -20.042, -15.534},
                                {-32.253, -31.469, -30.512, -29.398, -27.888, -25.699, -22.532, -18.087},
                                {-34.564, -33.789, -32.843, -31.746, -30.266, -28.114, -24.993, -20.603},
                                {-36.871, -36.105, -35.171, -34.088, -32.635, -30.52, -27.441, -23.099},
                                {-39.183, -38.426, -37.502, -36.434, -35.007, -32.925, -29.886, -25.588},
                                {-41.505, -40.756, -39.842, -38.787, -37.384, -35.335, -32.333, -28.077},
                                {-43.838, -43.096, -42.191, -41.15, -39.77, -37.751, -34.785, -30.567},
                                {-46.181, -45.446, -44.55, -43.521, -42.163, -40.173, -37.241, -33.06},
                                {-48.435, -47.803, -46.915, -45.897, -44.561, -42.598, -39.698, -35.553},
                                {-50.617, -50.162, -49.281, -48.275, -46.958, -45.021, -42.044, -38.041},
                                {-52.796, -52.517, -51.644, -50.648, -49.351, -47.205, -44.233, -40.46},
                                {-54.976, -54.812, -53.997, -53.011, -51.654, -49.389, -46.422, -42.661},
                                {-57.162, -56.998, -56.331, -55.356, -53.841, -51.579, -48.614, -44.864},
                                {-59.354, -59.19, -58.641, -57.654, -56.035, -53.774, -50.812, -47.072},
                                {-61.552, -61.389, -60.89, -59.854, -58.236, -55.975, -53.016, -49.284},
                                {-63.756, -63.593, -63.094, -62.058, -60.44, -58.181, -55.224, -51.498},
                                {-65.96, -65.797, -65.298, -64.263, -62.646, -60.387, -57.433, -53.712},
                                {-68.159, -67.997, -67.468, -66.463, -64.846, -62.589, -59.636, -55.92},
                                {-70.347, -70.184, -69.534, -68.61, -67.035, -64.778, -61.826, -58.115},
                                {-72.513, -72.345, -71.529, -70.612, -69.202, -66.946, -63.995, -60.289},
                                {-74.649, -74.259, -73.447, -72.538, -71.339, -69.083, -66.134, -62.431},
                                {-76.744, -76.091, -75.284, -74.383, -73.256, -71.179, -68.23, -64.53},
                                {-78.494, -77.838, -77.036, -76.142, -75.028, -73.221, -70.274, -66.577},
                                {-80.149, -79.497, -78.7, -77.812, -76.711, -75.06, -72.254, -68.559},
                                {-81.714, -81.065, -80.273, -79.392, -78.303, -76.669, -74.153, -70.42},
                                {-83.188, -82.543, -81.756, -80.881, -79.804, -78.185, -75.687, -71.974},
                        },

                },
        };
    }

    ;


    private static final class tabDataSeaWarm {
        private static final double[][][][] get = new double[][][][]{
                {
                        {//100 MHz, 1%, Warm Sea, Fig8
                                {101.483, 103.915, 106.062, 107.086, 107.254, 107.266, 107.266, 107.266},
                                {90.908, 93.933, 97.48, 100.526, 101.501, 101.607, 101.612, 101.613},
                                {84.475, 87.628, 91.529, 95.787, 98.095, 98.48, 98.506, 98.507},
                                {79.875, 83.064, 87.047, 91.786, 95.368, 96.283, 96.317, 96.317},
                                {76.277, 79.479, 83.473, 88.38, 92.881, 94.537, 94.653, 94.653},
                                {73.298, 76.51, 80.501, 85.46, 90.535, 93.028, 93.314, 93.314},
                                {70.736, 73.963, 77.951, 82.924, 88.333, 91.64, 92.194, 92.194},
                                {68.474, 71.72, 75.712, 80.69, 86.29, 90.315, 91.229, 91.229},
                                {66.436, 69.71, 73.712, 78.696, 84.407, 89.03, 90.337, 90.381},
                                {64.574, 67.881, 71.901, 76.897, 82.673, 87.769, 89.513, 89.622},
                                {62.856, 66.202, 70.245, 75.26, 81.076, 86.534, 88.741, 88.934},
                                {61.259, 64.647, 68.718, 73.758, 79.6, 85.33, 88.006, 88.303},
                                {59.767, 63.2, 67.303, 72.372, 78.232, 84.163, 87.296, 87.705},
                                {58.369, 61.848, 65.984, 71.087, 76.959, 83.037, 86.601, 87.146},
                                {57.057, 60.582, 64.751, 69.891, 75.771, 81.955, 85.915, 86.62},
                                {55.832, 59.4, 63.598, 68.774, 74.659, 80.916, 85.233, 86.122},
                                {54.686, 58.292, 62.517, 67.73, 73.613, 79.92, 84.56, 85.647},
                                {53.619, 57.257, 61.591, 66.785, 72.643, 78.976, 83.904, 85.193},
                                {52.746, 56.378, 60.611, 65.833, 71.698, 78.051, 83.232, 84.758},
                                {51.721, 55.399, 59.75, 64.971, 70.818, 77.173, 82.571, 84.332},
                                {48.389, 51.952, 56.312, 61.378, 67.04, 73.278, 79.339, 82.357},
                                {46.275, 49.645, 53.866, 58.629, 63.998, 70.001, 76.278, 80.454},
                                {45.095, 48.201, 52.056, 56.465, 61.504, 67.234, 73.502, 78.553},
                                {44.308, 47.106, 50.613, 54.683, 59.401, 64.856, 71.012, 76.673},
                                {43.705, 46.202, 49.405, 53.17, 57.597, 62.789, 68.784, 74.809},
                                {43.187, 45.414, 48.358, 51.86, 56.026, 60.973, 66.785, 72.989},
                                {42.706, 44.699, 47.429, 50.707, 54.644, 59.366, 64.985, 71.235},
                                {42.234, 44.033, 46.588, 49.679, 53.416, 57.932, 63.356, 69.561},
                                {41.757, 43.4, 45.815, 48.75, 52.316, 56.645, 61.876, 67.975},
                                {41.267, 42.791, 45.096, 47.903, 51.321, 55.48, 60.524, 66.478},
                                {40.763, 42.202, 44.419, 47.121, 50.412, 54.419, 59.285, 65.071},
                                {40.246, 41.631, 43.778, 46.392, 49.574, 53.444, 58.142, 63.751},
                                {39.721, 41.077, 43.168, 45.708, 48.793, 52.54, 57.081, 62.513},
                                {39.193, 40.539, 42.582, 45.059, 48.059, 51.695, 56.091, 61.353},
                                {38.665, 40.019, 42.02, 44.439, 47.364, 50.898, 55.162, 60.263},
                                {38.144, 39.515, 41.477, 43.844, 46.699, 50.14, 54.284, 59.238},
                                {37.124, 38.551, 40.441, 42.712, 45.441, 48.72, 52.654, 57.352},
                                {36.138, 37.638, 39.458, 41.64, 44.258, 47.396, 51.157, 55.645},
                                {35.305, 36.765, 38.515, 40.614, 43.13, 46.146, 49.759, 54.074},
                                {34.518, 35.921, 37.604, 39.624, 42.047, 44.954, 48.439, 52.608},
                                {33.752, 35.1, 36.719, 38.665, 41.002, 43.81, 47.182, 51.224},
                                {33.001, 34.297, 35.856, 37.732, 39.99, 42.708, 45.977, 49.904},
                                {32.26, 33.508, 35.011, 36.824, 39.008, 41.642, 44.815, 48.634},
                                {31.537, 32.736, 34.186, 35.937, 38.053, 40.609, 43.696, 47.422},
                                {30.825, 31.98, 33.378, 35.071, 37.122, 39.607, 42.615, 46.256},
                                {30.125, 31.237, 32.586, 34.225, 36.216, 38.633, 41.568, 45.129},
                                {28.422, 29.436, 30.677, 32.193, 34.046, 36.312, 39.081, 42.464},
                                {26.785, 27.717, 28.862, 30.272, 32.006, 34.139, 36.763, 39.99},
                                {25.217, 26.076, 27.138, 28.454, 30.082, 32.098, 34.593, 37.681},
                                {23.717, 24.511, 25.5, 26.732, 28.266, 30.176, 32.556, 35.518},
                                {22.272, 23.016, 23.947, 25.111, 26.565, 28.385, 30.66, 33.504},
                                {20.909, 21.594, 22.458, 23.547, 24.919, 26.648, 28.827, 31.573},
                                {19.597, 20.236, 21.045, 22.072, 23.373, 25.023, 27.115, 29.767},
                                {18.343, 18.939, 19.699, 20.668, 21.905, 23.481, 25.493, 28.057},
                                {17.144, 17.701, 18.415, 19.332, 20.508, 22.018, 23.954, 26.438},
                                {15.998, 16.519, 17.191, 18.06, 19.18, 20.626, 22.493, 24.902},
                                {14.897, 15.391, 16.031, 16.86, 17.934, 19.326, 21.129, 23.464},
                                {13.857, 14.315, 14.912, 15.693, 16.713, 18.045, 19.786, 22.059},
                                {12.859, 13.289, 13.852, 14.594, 15.568, 16.848, 18.531, 20.743},
                                {11.908, 12.311, 12.843, 13.547, 14.479, 15.71, 17.339, 19.492},
                                {11.002, 11.38, 11.883, 12.552, 13.442, 14.628, 16.205, 18.304},
                                {10.139, 10.494, 10.969, 11.605, 12.457, 13.599, 15.127, 17.174},
                                {9.319, 9.652, 10.1, 10.706, 11.521, 12.621, 14.103, 16.101},
                                {8.539, 8.851, 9.276, 9.851, 10.632, 11.692, 13.13, 15.082},
                                {7.798, 8.092, 8.493, 9.04, 9.788, 10.81, 12.206, 14.114},
                                {7.096, 7.371, 7.75, 8.271, 8.987, 9.973, 11.329, 13.194},
                                {6.43, 6.688, 7.046, 7.541, 8.228, 9.179, 10.496, 12.321},
                                {5.799, 6.041, 6.378, 6.85, 7.507, 8.425, 9.706, 11.492},
                                {5.201, 5.428, 5.746, 6.195, 6.825, 7.71, 8.955, 10.706},
                                {4.635, 4.847, 5.148, 5.574, 6.178, 7.033, 8.243, 9.958},
                                {4.099, 4.298, 4.581, 4.986, 5.564, 6.39, 7.568, 9.249},
                                {3.592, 3.778, 4.045, 4.43, 4.983, 5.78, 6.926, 8.575},
                                {3.112, 3.285, 3.537, 3.902, 4.432, 5.201, 6.317, 7.935},
                                {2.657, 2.819, 3.056, 3.403, 3.91, 4.653, 5.738, 7.327},
                                {2.227, 2.378, 2.601, 2.929, 3.415, 4.131, 5.189, 6.749},
                                {1.819, 1.959, 2.169, 2.481, 2.945, 3.636, 4.666, 6.198},
                                {1.432, 1.563, 1.759, 2.055, 2.498, 3.165, 4.168, 5.674},
                                {1.065, 1.187, 1.371, 1.65, 2.074, 2.717, 3.694, 5.175},
                        },
                        {//600 MHz, 1%, Warm Sea, Fig16
                                {107.073, 107.208, 107.262, 107.266, 107.266, 107.266, 107.266, 107.266},
                                {100.311, 101.201, 101.593, 101.612, 101.613, 101.613, 101.613, 101.613},
                                {95.293, 97.276, 98.437, 98.506, 98.507, 98.507, 98.507, 98.507},
                                {91.161, 93.962, 96.157, 96.317, 96.317, 96.317, 96.317, 96.317},
                                {87.749, 91.04, 94.236, 94.653, 94.653, 94.653, 94.653, 94.653},
                                {84.887, 88.465, 92.437, 93.314, 93.314, 93.314, 93.314, 93.314},
                                {82.437, 86.195, 90.673, 92.194, 92.194, 92.194, 92.194, 92.194},
                                {80.301, 84.181, 88.941, 91.229, 91.229, 91.229, 91.229, 91.229},
                                {78.41, 82.378, 87.271, 90.344, 90.381, 90.381, 90.381, 90.381},
                                {76.715, 80.752, 85.691, 89.502, 89.622, 89.622, 89.622, 89.622},
                                {75.18, 79.272, 84.215, 88.691, 88.934, 88.934, 88.934, 88.934},
                                {73.777, 77.915, 82.846, 87.887, 88.304, 88.304, 88.304, 88.304},
                                {72.486, 76.664, 81.579, 87.076, 87.718, 87.72, 87.72, 87.72},
                                {71.29, 75.504, 80.407, 86.249, 87.164, 87.17, 87.17, 87.17},
                                {70.176, 74.422, 79.321, 85.407, 86.644, 86.654, 86.654, 86.654},
                                {69.134, 73.408, 78.313, 84.555, 86.154, 86.17, 86.17, 86.17},
                                {68.154, 72.456, 77.374, 83.703, 85.687, 85.712, 85.712, 85.712},
                                {67.231, 71.557, 76.499, 82.861, 85.241, 85.279, 85.279, 85.279},
                                {66.419, 70.744, 75.68, 82.037, 84.811, 84.867, 84.867, 84.867},
                                {65.696, 70, 74.911, 81.237, 84.393, 84.475, 84.475, 84.475},
                                {62.888, 66.997, 71.679, 77.714, 82.401, 82.738, 82.738, 82.738},
                                {60.824, 64.742, 69.203, 74.955, 80.422, 81.26, 81.26, 81.26},
                                {59.256, 63.005, 67.271, 72.774, 78.456, 79.982, 79.982, 79.982},
                                {58.126, 61.697, 65.761, 71.003, 76.606, 78.856, 78.856, 78.856},
                                {57.364, 60.739, 64.58, 69.535, 74.931, 77.79, 77.853, 77.853},
                                {56.873, 60.043, 63.652, 68.306, 73.442, 76.745, 76.949, 76.949},
                                {56.553, 59.525, 62.909, 67.272, 72.124, 75.722, 76.128, 76.128},
                                {56.33, 59.12, 62.299, 66.397, 70.958, 74.717, 75.376, 75.376},
                                {56.155, 58.785, 61.783, 65.647, 69.927, 73.733, 74.682, 74.682},
                                {56, 58.491, 61.333, 64.993, 69.012, 72.772, 74.029, 74.04},
                                {55.85, 58.22, 60.927, 64.411, 68.196, 71.843, 73.361, 73.441},
                                {55.695, 57.962, 60.551, 63.883, 67.46, 70.953, 72.711, 72.853},
                                {55.532, 57.707, 60.194, 63.393, 66.789, 70.109, 72.074, 72.284},
                                {55.441, 57.453, 59.85, 62.93, 66.17, 69.313, 71.441, 71.741},
                                {55.341, 57.216, 59.512, 62.487, 65.589, 68.567, 70.806, 71.221},
                                {55.227, 57.021, 59.178, 62.057, 65.039, 67.866, 70.166, 70.72},
                                {54.957, 56.606, 58.547, 61.218, 63.998, 66.577, 68.868, 69.766},
                                {54.637, 56.158, 57.954, 60.387, 63.004, 65.398, 67.576, 68.853},
                                {54.276, 55.682, 57.348, 59.564, 62.03, 64.285, 66.328, 67.947},
                                {53.881, 55.183, 56.731, 58.784, 61.061, 63.207, 65.132, 66.995},
                                {53.461, 54.668, 56.107, 58.012, 60.089, 62.144, 63.975, 65.951},
                                {53.023, 54.142, 55.481, 57.248, 59.111, 61.084, 62.84, 64.817},
                                {52.572, 53.61, 54.855, 56.495, 58.127, 60.022, 61.712, 63.618},
                                {52.113, 53.075, 54.232, 55.752, 57.137, 58.955, 60.582, 62.382},
                                {51.649, 52.539, 53.614, 55.021, 56.276, 57.882, 59.445, 61.123},
                                {51.183, 52.005, 53.001, 54.302, 55.469, 56.803, 58.298, 59.848},
                                {50.016, 50.684, 51.498, 52.554, 53.516, 54.377, 55.377, 56.593},
                                {48.858, 49.387, 50.038, 50.876, 51.651, 52.352, 52.973, 53.53},
                                {47.687, 48.103, 48.619, 49.276, 49.897, 50.466, 50.959, 51.342},
                                {46.416, 46.781, 47.234, 47.811, 48.368, 48.888, 49.342, 49.67},
                                {45.142, 45.471, 45.878, 46.397, 46.907, 47.393, 47.82, 48.114},
                                {43.881, 44.178, 44.545, 45.014, 45.483, 45.937, 46.336, 46.597},
                                {42.627, 42.897, 43.23, 43.656, 44.09, 44.514, 44.884, 45.113},
                                {41.378, 41.626, 41.929, 42.319, 42.722, 43.118, 43.46, 43.658},
                                {40.133, 40.361, 40.639, 40.998, 41.372, 41.743, 42.058, 42.228},
                                {38.891, 39.102, 39.358, 39.69, 40.039, 40.385, 40.674, 40.819},
                                {37.652, 37.848, 38.085, 38.393, 38.719, 39.041, 39.305, 39.429},
                                {36.416, 36.599, 36.818, 37.106, 37.411, 37.711, 37.95, 38.055},
                                {35.184, 35.356, 35.56, 35.828, 36.114, 36.392, 36.607, 36.698},
                                {33.958, 34.119, 34.309, 34.561, 34.828, 35.085, 35.279, 35.357},
                                {32.739, 32.89, 33.068, 33.304, 33.555, 33.792, 33.966, 34.035},
                                {31.529, 31.67, 31.838, 32.06, 32.294, 32.512, 32.668, 32.73},
                                {30.329, 30.462, 30.619, 30.828, 31.047, 31.249, 31.389, 31.447},
                                {29.141, 29.267, 29.415, 29.612, 29.817, 30.003, 30.13, 30.184},
                                {27.967, 28.086, 28.226, 28.412, 28.604, 28.775, 28.891, 28.944},
                                {26.809, 26.922, 27.054, 27.23, 27.41, 27.568, 27.675, 27.727},
                                {25.669, 25.776, 25.9, 26.067, 26.236, 26.384, 26.482, 26.534},
                                {24.548, 24.649, 24.767, 24.926, 25.085, 25.222, 25.314, 25.367},
                                {23.447, 23.543, 23.655, 23.806, 23.956, 24.085, 24.172, 24.225},
                                {22.367, 22.459, 22.566, 22.71, 22.852, 22.973, 23.056, 23.11},
                                {21.311, 21.399, 21.501, 21.638, 21.773, 21.887, 21.967, 22.022},
                                {20.278, 20.362, 20.46, 20.592, 20.72, 20.828, 20.906, 20.962},
                                {19.27, 19.351, 19.445, 19.572, 19.694, 19.797, 19.873, 19.93},
                                {18.288, 18.365, 18.456, 18.578, 18.695, 18.794, 18.868, 18.926},
                                {17.331, 17.406, 17.494, 17.611, 17.724, 17.819, 17.892, 17.951},
                                {16.401, 16.474, 16.558, 16.672, 16.781, 16.873, 16.944, 17.004},
                                {15.498, 15.568, 15.65, 15.761, 15.866, 15.955, 16.026, 16.086},
                                {14.622, 14.69, 14.77, 14.877, 14.979, 15.066, 15.136, 15.197},
                        },
                        {//2000 MHz, 1%, Warm Sea, Fig24
                                {107.266, 107.266, 107.266, 107.266, 107.266, 107.266, 107.266, 107.266},
                                {101.613, 101.613, 101.613, 101.613, 101.613, 101.613, 101.613, 101.613},
                                {98.507, 98.507, 98.507, 98.507, 98.507, 98.507, 98.507, 98.507},
                                {96.317, 96.317, 96.317, 96.317, 96.317, 96.317, 96.317, 96.317},
                                {94.653, 94.653, 94.653, 94.653, 94.653, 94.653, 94.653, 94.653},
                                {93.314, 93.314, 93.314, 93.314, 93.314, 93.314, 93.314, 93.314},
                                {92.194, 92.194, 92.194, 92.194, 92.194, 92.194, 92.194, 92.194},
                                {91.229, 91.229, 91.229, 91.229, 91.229, 91.229, 91.229, 91.229},
                                {90.381, 90.381, 90.381, 90.381, 90.381, 90.381, 90.381, 90.381},
                                {89.622, 89.622, 89.622, 89.622, 89.622, 89.622, 89.622, 89.622},
                                {88.934, 88.934, 88.934, 88.934, 88.934, 88.934, 88.934, 88.934},
                                {88.304, 88.304, 88.304, 88.304, 88.304, 88.304, 88.304, 88.304},
                                {87.72, 87.72, 87.72, 87.72, 87.72, 87.72, 87.72, 87.72},
                                {87.17, 87.17, 87.17, 87.17, 87.17, 87.17, 87.17, 87.17},
                                {86.654, 86.654, 86.654, 86.654, 86.654, 86.654, 86.654, 86.654},
                                {86.17, 86.17, 86.17, 86.17, 86.17, 86.17, 86.17, 86.17},
                                {85.712, 85.712, 85.712, 85.712, 85.712, 85.712, 85.712, 85.712},
                                {85.279, 85.279, 85.279, 85.279, 85.279, 85.279, 85.279, 85.279},
                                {84.867, 84.867, 84.867, 84.867, 84.867, 84.867, 84.867, 84.867},
                                {84.475, 84.475, 84.475, 84.475, 84.475, 84.475, 84.475, 84.475},
                                {82.738, 82.738, 82.738, 82.738, 82.738, 82.738, 82.738, 82.738},
                                {81.26, 81.26, 81.26, 81.26, 81.26, 81.26, 81.26, 81.26},
                                {79.982, 79.982, 79.982, 79.982, 79.982, 79.982, 79.982, 79.982},
                                {78.856, 78.856, 78.856, 78.856, 78.856, 78.856, 78.856, 78.856},
                                {77.853, 77.853, 77.853, 77.853, 77.853, 77.853, 77.853, 77.853},
                                {76.949, 76.949, 76.949, 76.949, 76.949, 76.949, 76.949, 76.949},
                                {76.128, 76.128, 76.128, 76.128, 76.128, 76.128, 76.128, 76.128},
                                {75.376, 75.376, 75.376, 75.376, 75.376, 75.376, 75.376, 75.376},
                                {74.682, 74.682, 74.682, 74.682, 74.682, 74.682, 74.682, 74.682},
                                {74.04, 74.04, 74.04, 74.04, 74.04, 74.04, 74.04, 74.04},
                                {73.417, 73.417, 73.417, 73.417, 73.417, 73.417, 73.417, 73.417},
                                {72.81, 72.81, 72.81, 72.81, 72.81, 72.81, 72.81, 72.81},
                                {72.231, 72.231, 72.231, 72.231, 72.231, 72.231, 72.231, 72.231},
                                {71.677, 71.677, 71.677, 71.677, 71.677, 71.677, 71.677, 71.677},
                                {71.145, 71.145, 71.145, 71.145, 71.145, 71.145, 71.145, 71.145},
                                {70.632, 70.632, 70.632, 70.632, 70.632, 70.632, 70.632, 70.632},
                                {69.656, 69.656, 69.656, 69.656, 69.656, 69.656, 69.656, 69.656},
                                {68.737, 68.737, 68.737, 68.737, 68.737, 68.737, 68.737, 68.737},
                                {67.87, 67.87, 67.87, 67.87, 67.87, 67.87, 67.87, 67.87},
                                {67.043, 67.043, 67.043, 67.043, 67.043, 67.043, 67.043, 67.043},
                                {66.249, 66.249, 66.249, 66.249, 66.249, 66.249, 66.249, 66.249},
                                {65.483, 65.483, 65.483, 65.483, 65.483, 65.483, 65.483, 65.483},
                                {64.741, 64.741, 64.741, 64.741, 64.741, 64.741, 64.741, 64.741},
                                {64.019, 64.019, 64.019, 64.019, 64.019, 64.019, 64.019, 64.019},
                                {63.315, 63.315, 63.315, 63.315, 63.315, 63.315, 63.315, 63.315},
                                {62.626, 62.626, 62.626, 62.626, 62.626, 62.626, 62.626, 62.626},
                                {60.962, 60.962, 60.962, 60.962, 60.962, 60.962, 60.962, 60.962},
                                {59.368, 59.368, 59.368, 59.368, 59.368, 59.368, 59.368, 59.368},
                                {57.834, 57.834, 57.834, 57.834, 57.834, 57.834, 57.834, 57.834},
                                {56.358, 56.358, 56.358, 56.358, 56.358, 56.358, 56.358, 56.358},
                                {54.938, 54.938, 54.938, 54.938, 54.938, 54.938, 54.938, 54.938},
                                {53.572, 53.572, 53.572, 53.572, 53.572, 53.572, 53.572, 53.572},
                                {52.256, 52.256, 52.256, 52.256, 52.256, 52.256, 52.256, 52.256},
                                {50.986, 50.986, 50.986, 50.986, 50.986, 50.986, 50.986, 50.986},
                                {49.753, 49.753, 49.753, 49.753, 49.753, 49.753, 49.753, 49.753},
                                {48.551, 48.551, 48.551, 48.551, 48.551, 48.551, 48.551, 48.551},
                                {47.371, 47.371, 47.371, 47.371, 47.371, 47.371, 47.371, 47.371},
                                {46.21, 46.21, 46.21, 46.21, 46.21, 46.21, 46.21, 46.21},
                                {45.061, 45.061, 45.061, 45.061, 45.061, 45.061, 45.061, 45.061},
                                {43.921, 43.921, 43.921, 43.921, 43.921, 43.921, 43.921, 43.921},
                                {42.789, 42.789, 42.789, 42.789, 42.789, 42.789, 42.789, 42.789},
                                {41.664, 41.664, 41.664, 41.664, 41.664, 41.664, 41.664, 41.664},
                                {40.543, 40.543, 40.543, 40.543, 40.543, 40.543, 40.543, 40.543},
                                {39.428, 39.428, 39.428, 39.428, 39.428, 39.428, 39.428, 39.428},
                                {38.318, 38.318, 38.318, 38.318, 38.318, 38.318, 38.318, 38.318},
                                {37.214, 37.214, 37.214, 37.214, 37.214, 37.214, 37.214, 37.214},
                                {36.114, 36.114, 36.114, 36.114, 36.114, 36.114, 36.114, 36.114},
                                {35.019, 35.019, 35.019, 35.019, 35.019, 35.019, 35.019, 35.019},
                                {33.928, 33.928, 33.928, 33.928, 33.928, 33.928, 33.928, 33.928},
                                {32.842, 32.842, 32.842, 32.842, 32.842, 32.842, 32.842, 32.842},
                                {31.76, 31.76, 31.76, 31.76, 31.76, 31.76, 31.76, 31.76},
                                {30.683, 30.683, 30.683, 30.683, 30.683, 30.683, 30.683, 30.683},
                                {29.611, 29.611, 29.611, 29.611, 29.611, 29.611, 29.611, 29.611},
                                {28.545, 28.545, 28.545, 28.545, 28.545, 28.545, 28.545, 28.545},
                                {27.485, 27.485, 27.485, 27.485, 27.485, 27.485, 27.485, 27.485},
                                {26.433, 26.433, 26.433, 26.433, 26.433, 26.433, 26.433, 26.433},
                                {25.39, 25.39, 25.39, 25.39, 25.39, 25.39, 25.39, 25.39},
                                {24.357, 24.357, 24.357, 24.357, 24.357, 24.357, 24.357, 24.357},
                        },
                },
                {
                        {//100 MHz, 10%, Warm Sea, Fig7
                                {97.935, 102.299, 105.726, 106.905, 107.062, 107.074, 107.074, 107.074},
                                {88.379, 92.582, 97.061, 100.207, 101.065, 101.156, 101.161, 101.161},
                                {82.648, 86.625, 91.196, 95.487, 97.471, 97.78, 97.802, 97.803},
                                {78.482, 82.298, 86.789, 91.566, 94.68, 95.385, 95.445, 95.448},
                                {75.167, 78.869, 83.265, 88.227, 92.231, 93.495, 93.625, 93.632},
                                {72.384, 76.007, 80.323, 85.349, 89.974, 91.894, 92.137, 92.15},
                                {69.962, 73.534, 77.789, 82.835, 87.867, 90.47, 90.874, 90.9},
                                {67.801, 71.343, 75.557, 80.609, 85.903, 89.156, 89.772, 89.815},
                                {65.838, 69.367, 73.556, 78.612, 84.076, 87.912, 88.789, 88.856},
                                {64.03, 67.56, 71.738, 76.802, 82.38, 86.716, 87.897, 87.997},
                                {62.344, 65.888, 70.067, 75.144, 80.802, 85.557, 87.075, 87.217},
                                {60.761, 64.327, 68.518, 73.615, 79.332, 84.431, 86.307, 86.502},
                                {59.263, 62.86, 67.072, 72.194, 77.959, 83.337, 85.582, 85.843},
                                {57.838, 61.473, 65.714, 70.865, 76.67, 82.275, 84.891, 85.229},
                                {56.477, 60.155, 64.432, 69.617, 75.458, 81.245, 84.226, 84.654},
                                {55.171, 58.897, 63.218, 68.438, 74.312, 80.248, 83.582, 84.113},
                                {53.916, 57.694, 62.062, 67.322, 73.227, 79.284, 82.953, 83.602},
                                {52.706, 56.539, 60.96, 66.26, 72.196, 78.352, 82.338, 83.116},
                                {51.537, 55.428, 59.951, 65.28, 71.224, 77.459, 81.74, 82.654},
                                {50.408, 54.357, 58.896, 64.278, 70.273, 76.582, 81.137, 82.209},
                                {45.287, 49.527, 54.407, 59.972, 66.099, 72.632, 78.245, 80.211},
                                {40.877, 45.505, 50.635, 56.347, 62.569, 69.218, 75.482, 78.452},
                                {37.447, 42.233, 47.437, 53.235, 59.497, 66.204, 72.863, 76.82},
                                {34.847, 39.579, 44.764, 50.533, 56.772, 63.489, 70.39, 75.246},
                                {32.884, 37.442, 42.535, 48.179, 54.328, 61.008, 68.055, 73.693},
                                {31.428, 35.76, 40.691, 46.129, 52.125, 58.724, 65.852, 72.149},
                                {30.402, 34.531, 39.155, 44.333, 50.13, 56.614, 63.773, 70.609},
                                {29.71, 33.49, 37.792, 42.69, 48.265, 54.604, 61.755, 69.027},
                                {29.169, 32.607, 36.588, 41.198, 46.537, 52.717, 59.829, 67.436},
                                {28.679, 31.799, 35.476, 39.807, 44.91, 50.919, 57.969, 65.824},
                                {28.196, 31.03, 34.425, 38.492, 43.364, 49.198, 56.167, 64.198},
                                {27.699, 30.278, 33.417, 37.238, 41.887, 47.546, 54.42, 62.564},
                                {27.182, 29.535, 32.442, 36.035, 40.474, 45.958, 52.725, 60.932},
                                {26.642, 28.796, 31.496, 34.879, 39.118, 44.431, 51.082, 59.311},
                                {26.083, 28.062, 30.575, 33.766, 37.818, 42.963, 49.493, 57.711},
                                {25.508, 27.333, 29.679, 32.695, 36.572, 41.555, 47.958, 56.137},
                                {24.333, 25.903, 27.965, 30.674, 34.233, 38.909, 45.051, 53.095},
                                {23.151, 24.521, 26.353, 28.806, 32.089, 36.482, 42.36, 50.214},
                                {21.984, 23.195, 24.842, 27.081, 30.123, 34.257, 39.877, 47.508},
                                {20.846, 21.931, 23.425, 25.483, 28.316, 32.216, 37.586, 44.976},
                                {19.742, 20.726, 22.094, 23.998, 26.648, 30.336, 35.469, 42.61},
                                {18.668, 19.57, 20.836, 22.611, 25.101, 28.594, 33.493, 40.364},
                                {17.603, 18.448, 19.636, 21.308, 23.659, 26.966, 31.618, 38.161},
                                {16.591, 17.384, 18.503, 20.082, 22.312, 25.46, 29.904, 36.178},
                                {15.621, 16.367, 17.424, 18.921, 21.044, 24.051, 28.311, 34.348},
                                {14.683, 15.388, 16.391, 17.816, 19.842, 22.722, 26.817, 32.637},
                                {12.45, 13.076, 13.971, 15.25, 17.078, 19.689, 23.421, 28.753},
                                {10.344, 10.917, 11.736, 12.907, 14.583, 16.979, 20.406, 25.307},
                                {8.34, 8.876, 9.641, 10.733, 12.29, 14.511, 17.681, 22.202},
                                {6.423, 6.935, 7.661, 8.692, 10.155, 12.233, 15.184, 19.372},
                                {4.582, 5.077, 5.775, 6.76, 8.148, 10.107, 12.871, 16.769},
                                {2.809, 3.293, 3.97, 4.919, 6.247, 8.106, 10.709, 14.353},
                                {1.098, 1.575, 2.237, 3.157, 4.434, 6.209, 8.674, 12.097},
                                {-0.558, -0.085, 0.566, 1.463, 2.699, 4.402, 6.747, 9.977},
                                {-2.161, -1.69, -1.047, -0.168, 1.032, 2.672, 4.913, 7.974},
                                {-3.716, -3.246, -2.608, -1.744, -0.574, 1.012, 3.161, 6.074},
                                {-5.226, -4.754, -4.121, -3.269, -2.125, -0.586, 1.483, 4.264},
                                {-6.692, -6.219, -5.588, -4.747, -3.624, -2.127, -0.129, 2.536},
                                {-8.116, -7.642, -7.013, -6.18, -5.077, -3.616, -1.681, 0.881},
                                {-9.501, -9.024, -8.396, -7.57, -6.485, -5.056, -3.178, -0.708},
                                {-10.848, -10.368, -9.741, -8.921, -7.85, -6.451, -4.623, -2.235},
                                {-12.158, -11.675, -11.048, -10.233, -9.176, -7.803, -6.02, -3.705},
                                {-13.432, -12.946, -12.318, -11.508, -10.462, -9.113, -7.371, -5.122},
                                {-14.67, -14.181, -13.553, -12.747, -11.712, -10.384, -8.679, -6.49},
                                {-15.875, -15.382, -14.754, -13.951, -12.926, -11.617, -9.945, -7.81},
                                {-17.046, -16.55, -15.921, -15.121, -14.105, -12.813, -11.172, -9.086},
                                {-18.184, -17.686, -17.055, -16.258, -15.249, -13.974, -12.36, -10.319},
                                {-19.29, -18.789, -18.157, -17.362, -16.361, -15.1, -13.512, -11.511},
                                {-20.364, -19.86, -19.228, -18.435, -17.44, -16.193, -14.628, -12.665},
                                {-21.408, -20.901, -20.268, -19.477, -18.488, -17.253, -15.709, -13.78},
                                {-22.421, -21.912, -21.277, -20.488, -19.505, -18.281, -16.757, -14.86},
                                {-23.405, -22.893, -22.257, -21.469, -20.491, -19.278, -17.772, -15.904},
                                {-24.359, -23.845, -23.208, -22.421, -21.448, -20.244, -18.755, -16.914},
                                {-25.285, -24.768, -24.131, -23.345, -22.376, -21.181, -19.708, -17.891},
                                {-26.183, -25.663, -25.025, -24.24, -23.275, -22.089, -20.63, -18.837},
                                {-27.053, -26.532, -25.892, -25.109, -24.147, -22.969, -21.524, -19.751},
                                {-27.896, -27.373, -26.733, -25.95, -24.992, -23.821, -22.388, -20.636},
                                {-28.714, -28.188, -27.547, -26.765, -25.81, -24.646, -23.225, -21.491},
                        },
                        {//600 MHz, 10%, Warm Sea, Fig15
                                {107.039, 107.069, 107.073, 107.074, 107.074, 107.074, 107.074, 107.074},
                                {100.292, 101.062, 101.149, 101.161, 101.161, 101.161, 101.161, 101.161},
                                {94.553, 97.266, 97.742, 97.802, 97.803, 97.803, 97.803, 97.803},
                                {89.791, 93.922, 95.258, 95.445, 95.448, 95.448, 95.448, 95.448},
                                {86.015, 90.721, 93.184, 93.623, 93.632, 93.632, 93.632, 93.632},
                                {82.917, 87.76, 91.283, 92.128, 92.15, 92.15, 92.15, 92.15},
                                {80.294, 85.096, 89.444, 90.847, 90.9, 90.9, 90.9, 90.9},
                                {78.02, 82.718, 87.636, 89.707, 89.814, 89.816, 89.816, 89.816},
                                {76.012, 80.636, 85.866, 88.658, 88.855, 88.859, 88.859, 88.859},
                                {74.214, 78.746, 84.152, 87.657, 87.993, 88.001, 88.001, 88.001},
                                {72.583, 77.018, 82.51, 86.67, 87.209, 87.223, 87.224, 87.224},
                                {71.089, 75.427, 80.947, 85.672, 86.488, 86.512, 86.512, 86.512},
                                {69.709, 73.949, 79.464, 84.646, 85.815, 85.856, 85.857, 85.857},
                                {68.423, 72.567, 78.058, 83.584, 85.183, 85.247, 85.248, 85.248},
                                {67.215, 71.265, 76.724, 82.487, 84.58, 84.678, 84.68, 84.68},
                                {66.073, 70.032, 75.456, 81.363, 83.998, 84.144, 84.148, 84.148},
                                {64.986, 68.857, 74.249, 80.225, 83.429, 83.641, 83.646, 83.646},
                                {63.944, 67.731, 73.097, 79.084, 82.865, 83.165, 83.172, 83.172},
                                {62.94, 66.647, 71.994, 77.953, 82.299, 82.712, 82.723, 82.723},
                                {61.967, 65.599, 70.936, 76.841, 81.724, 82.28, 82.295, 82.295},
                                {57.403, 60.761, 66.191, 71.755, 78.551, 80.348, 80.422, 80.423},
                                {53.168, 56.971, 62.164, 67.563, 74.882, 78.612, 78.87, 78.876},
                                {49.392, 53.728, 58.754, 64.141, 71.189, 76.829, 77.533, 77.554},
                                {46.739, 51.192, 55.943, 61.317, 67.864, 74.804, 76.337, 76.396},
                                {44.651, 49.017, 53.676, 58.957, 65.007, 72.488, 75.219, 75.364},
                                {43.082, 47.316, 51.835, 56.964, 62.576, 70.004, 74.113, 74.43},
                                {41.831, 45.924, 50.293, 55.256, 60.493, 67.53, 72.948, 73.572},
                                {40.755, 44.719, 48.951, 53.76, 58.69, 65.19, 71.658, 72.772},
                                {39.78, 43.633, 47.746, 52.422, 57.106, 63.043, 70.203, 72.012},
                                {38.874, 42.631, 46.642, 51.202, 55.695, 61.105, 68.584, 71.274},
                                {38.024, 41.697, 45.618, 50.078, 54.419, 59.377, 66.841, 70.537},
                                {37.225, 40.823, 44.663, 49.033, 53.253, 57.844, 65.035, 69.778},
                                {36.472, 40.002, 43.77, 48.058, 52.177, 56.486, 63.222, 68.972},
                                {35.764, 39.231, 42.932, 47.144, 51.176, 55.275, 61.454, 68.096},
                                {35.098, 38.507, 42.145, 46.286, 50.242, 54.183, 59.773, 67.13},
                                {34.472, 37.826, 41.405, 45.479, 49.365, 53.186, 58.219, 66.067},
                                {33.328, 36.579, 40.05, 44.001, 47.762, 51.411, 55.583, 63.657},
                                {32.307, 35.465, 38.837, 42.676, 46.325, 49.849, 53.532, 60.955},
                                {31.387, 34.46, 37.74, 41.475, 45.023, 48.444, 51.871, 58.123},
                                {30.546, 33.54, 36.735, 40.374, 43.828, 47.158, 50.44, 55.485},
                                {29.766, 32.686, 35.803, 39.352, 42.718, 45.965, 49.149, 53.356},
                                {29.032, 31.883, 34.926, 38.392, 41.675, 44.844, 47.951, 51.707},
                                {28.332, 31.118, 34.091, 37.478, 40.684, 43.779, 46.819, 50.352},
                                {27.656, 30.38, 33.288, 36.6, 39.732, 42.759, 45.738, 49.153},
                                {26.996, 29.662, 32.508, 35.75, 38.812, 41.773, 44.695, 48.038},
                                {26.346, 28.957, 31.744, 34.919, 37.916, 40.815, 43.682, 46.974},
                                {24.742, 27.228, 29.88, 32.902, 35.749, 38.503, 41.244, 44.439},
                                {23.142, 25.515, 28.048, 30.934, 33.646, 36.272, 38.898, 42.007},
                                {21.531, 23.803, 26.229, 28.993, 31.584, 34.094, 36.614, 39.641},
                                {19.908, 22.089, 24.418, 27.071, 29.553, 31.957, 34.379, 37.327},
                                {18.278, 20.376, 22.616, 25.169, 27.55, 29.857, 32.188, 35.058},
                                {16.647, 18.669, 20.828, 23.288, 25.578, 27.795, 30.041, 32.834},
                                {15.022, 16.974, 19.058, 21.433, 23.638, 25.772, 27.938, 30.657},
                                {13.407, 15.295, 17.309, 19.605, 21.732, 23.789, 25.881, 28.527},
                                {11.809, 13.636, 15.587, 17.809, 19.863, 21.848, 23.869, 26.446},
                                {10.231, 12.002, 13.893, 16.047, 18.033, 19.95, 21.905, 24.414},
                                {8.677, 10.395, 12.23, 14.32, 16.242, 18.096, 19.988, 22.433},
                                {7.149, 8.818, 10.6, 12.63, 14.492, 16.287, 18.119, 20.502},
                                {5.649, 7.272, 9.004, 10.978, 12.784, 14.522, 16.299, 18.622},
                                {4.18, 5.759, 7.444, 9.365, 11.118, 12.803, 14.526, 16.792},
                                {2.741, 4.279, 5.921, 7.791, 9.494, 11.129, 12.801, 15.012},
                                {1.335, 2.834, 4.434, 6.257, 7.912, 9.499, 11.124, 13.282},
                                {-0.037, 1.424, 2.984, 4.762, 6.372, 7.914, 9.493, 11.601},
                                {-1.377, 0.05, 1.572, 3.307, 4.874, 6.373, 7.908, 9.968},
                                {-2.682, -1.289, 0.197, 1.891, 3.417, 4.876, 6.369, 8.382},
                                {-3.953, -2.593, -1.14, 0.515, 2.002, 3.421, 4.875, 6.843},
                                {-5.19, -3.86, -2.44, -0.822, 0.627, 2.009, 3.424, 5.35},
                                {-6.393, -5.092, -3.704, -2.121, -0.708, 0.639, 2.017, 3.901},
                                {-7.562, -6.289, -4.931, -3.382, -2.003, -0.691, 0.653, 2.497},
                                {-8.697, -7.451, -6.121, -4.606, -3.259, -1.979, -0.669, 1.136},
                                {-9.799, -8.579, -7.277, -5.792, -4.477, -3.229, -1.951, -0.182},
                                {-10.868, -9.672, -8.396, -6.942, -5.657, -4.439, -3.192, -1.46},
                                {-11.904, -10.732, -9.482, -8.057, -6.8, -5.612, -4.395, -2.696},
                                {-12.908, -11.759, -10.533, -9.136, -7.907, -6.747, -5.559, -3.894},
                                {-13.88, -12.753, -11.551, -10.181, -8.979, -7.845, -6.685, -5.052},
                                {-14.821, -13.716, -12.536, -11.192, -10.016, -8.908, -7.775, -6.173},
                                {-15.731, -14.647, -13.489, -12.17, -11.019, -9.937, -8.829, -7.258},
                                {-16.612, -15.547, -14.41, -13.115, -11.989, -10.931, -9.848, -8.306},
                        },
                        {//2000 MHz, 10%, Warm Sea, Fig23
                                {107.071, 107.074, 107.074, 107.074, 107.074, 107.074, 107.074, 107.074},
                                {101.147, 101.16, 101.161, 101.161, 101.161, 101.161, 101.161, 101.161},
                                {97.762, 97.797, 97.802, 97.803, 97.803, 97.803, 97.803, 97.803},
                                {95.365, 95.435, 95.447, 95.448, 95.448, 95.448, 95.448, 95.448},
                                {93.486, 93.607, 93.629, 93.632, 93.632, 93.632, 93.632, 93.632},
                                {91.923, 92.109, 92.145, 92.15, 92.15, 92.15, 92.15, 92.15},
                                {90.571, 90.834, 90.89, 90.899, 90.9, 90.9, 90.9, 90.9},
                                {89.387, 89.719, 89.8, 89.814, 89.816, 89.816, 89.816, 89.816},
                                {88.316, 88.722, 88.835, 88.856, 88.859, 88.859, 88.859, 88.859},
                                {87.334, 87.816, 87.967, 87.996, 88.001, 88.001, 88.001, 88.001},
                                {86.419, 86.981, 87.177, 87.217, 87.223, 87.224, 87.224, 87.224},
                                {85.56, 86.203, 86.45, 86.503, 86.511, 86.512, 86.512, 86.512},
                                {84.746, 85.47, 85.775, 85.844, 85.855, 85.856, 85.857, 85.857},
                                {83.969, 84.774, 85.144, 85.231, 85.246, 85.248, 85.248, 85.248},
                                {83.224, 84.108, 84.55, 84.658, 84.678, 84.68, 84.68, 84.68},
                                {82.504, 83.468, 83.987, 84.12, 84.144, 84.147, 84.148, 84.148},
                                {81.808, 82.848, 83.451, 83.611, 83.642, 83.646, 83.646, 83.646},
                                {81.132, 82.247, 82.938, 83.128, 83.167, 83.172, 83.172, 83.172},
                                {80.473, 81.66, 82.444, 82.669, 82.716, 82.722, 82.723, 82.723},
                                {79.83, 81.087, 81.967, 82.23, 82.287, 82.295, 82.295, 82.295},
                                {76.8, 78.452, 79.767, 80.273, 80.4, 80.421, 80.423, 80.423},
                                {74.009, 76.03, 77.756, 78.582, 78.825, 78.871, 78.876, 78.876},
                                {71.403, 73.735, 75.845, 77.045, 77.454, 77.543, 77.553, 77.554},
                                {68.957, 71.538, 74.002, 75.592, 76.221, 76.375, 76.396, 76.397},
                                {66.655, 69.428, 72.221, 74.184, 75.079, 75.327, 75.365, 75.367},
                                {64.484, 67.402, 70.505, 72.8, 73.994, 74.369, 74.433, 74.438},
                                {62.434, 65.609, 68.858, 71.431, 72.941, 73.48, 73.581, 73.59},
                                {60.496, 63.964, 67.284, 70.079, 71.903, 72.641, 72.795, 72.81},
                                {58.914, 62.426, 65.783, 68.747, 70.868, 71.839, 72.063, 72.087},
                                {57.508, 60.983, 64.354, 67.441, 69.831, 71.059, 71.376, 71.413},
                                {56.192, 59.626, 62.995, 66.168, 68.79, 70.292, 70.726, 70.78},
                                {54.956, 58.346, 61.701, 64.931, 67.746, 69.529, 70.106, 70.184},
                                {53.792, 57.138, 60.47, 63.734, 66.705, 68.764, 69.51, 69.62},
                                {52.693, 55.994, 59.299, 62.579, 65.671, 67.993, 68.932, 69.085},
                                {51.654, 54.91, 58.184, 61.466, 64.651, 67.214, 68.368, 68.574},
                                {50.671, 53.883, 57.123, 60.398, 63.648, 66.427, 67.813, 68.085},
                                {48.854, 51.982, 55.15, 58.39, 61.713, 64.837, 66.714, 67.163},
                                {47.217, 50.263, 53.359, 56.548, 59.889, 63.249, 65.612, 66.301},
                                {45.731, 48.701, 51.725, 54.856, 58.182, 61.688, 64.493, 65.483},
                                {44.372, 47.269, 50.224, 53.293, 56.585, 60.172, 63.352, 64.697},
                                {43.11, 45.94, 48.828, 51.835, 55.081, 58.706, 62.188, 63.928},
                                {41.922, 44.688, 47.512, 50.458, 53.653, 57.284, 61.001, 63.164},
                                {40.785, 43.49, 46.254, 49.141, 52.281, 55.9, 59.791, 62.391},
                                {39.683, 42.33, 45.037, 47.866, 50.951, 54.543, 58.56, 61.593},
                                {38.602, 41.195, 43.847, 46.62, 49.65, 53.208, 57.308, 60.755},
                                {37.537, 40.078, 42.677, 45.397, 48.372, 51.888, 56.039, 59.865},
                                {34.921, 37.342, 39.82, 42.414, 45.257, 48.655, 52.824, 57.369},
                                {32.387, 34.7, 37.067, 39.546, 42.265, 45.536, 49.635, 54.535},
                                {29.966, 32.181, 34.448, 36.821, 39.423, 42.57, 46.56, 51.555},
                                {27.678, 29.804, 31.978, 34.254, 36.748, 39.775, 43.645, 48.602},
                                {25.525, 27.567, 29.657, 31.843, 34.236, 37.151, 40.9, 45.763},
                                {23.492, 25.458, 27.468, 29.571, 31.871, 34.681, 38.312, 43.063},
                                {21.561, 23.455, 25.392, 27.417, 29.63, 32.341, 35.862, 40.495},
                                {19.713, 21.54, 23.408, 25.36, 27.491, 30.11, 33.526, 38.044},
                                {17.931, 19.695, 21.498, 23.382, 25.436, 27.968, 31.285, 35.692},
                                {16.201, 17.907, 19.649, 21.467, 23.449, 25.899, 29.122, 33.424},
                                {14.515, 16.164, 17.848, 19.606, 21.519, 23.891, 27.026, 31.227},
                                {12.863, 14.459, 16.089, 17.788, 19.636, 21.934, 24.985, 29.09},
                                {11.241, 12.786, 14.363, 16.007, 17.793, 20.022, 22.992, 27.005},
                                {9.643, 11.14, 12.668, 14.259, 15.986, 18.147, 21.042, 24.968},
                                {8.067, 9.518, 10.998, 12.539, 14.21, 16.307, 19.128, 22.971},
                                {6.511, 7.918, 9.353, 10.846, 12.462, 14.498, 17.249, 21.012},
                                {4.973, 6.337, 7.729, 9.175, 10.74, 12.717, 15.402, 19.088},
                                {3.452, 4.776, 6.125, 7.528, 9.043, 10.963, 13.583, 17.195},
                                {1.948, 3.232, 4.541, 5.901, 7.368, 9.234, 11.792, 15.333},
                                {0.459, 1.706, 2.976, 4.295, 5.715, 7.53, 10.028, 13.5},
                                {-1.014, 0.197, 1.429, 2.708, 4.085, 5.848, 8.289, 11.696},
                                {-2.47, -1.295, -0.099, 1.142, 2.475, 4.19, 6.575, 9.918},
                                {-3.911, -2.77, -1.608, -0.405, 0.887, 2.554, 4.885, 8.167},
                                {-5.335, -4.227, -3.099, -1.932, -0.68, 0.942, 3.22, 6.442},
                                {-6.742, -5.665, -4.571, -3.438, -2.226, -0.648, 1.58, 4.744},
                                {-8.131, -7.086, -6.023, -4.924, -3.749, -2.215, -0.036, 3.072},
                                {-9.502, -8.487, -7.455, -6.389, -5.251, -3.758, -1.627, 1.427},
                                {-10.854, -9.868, -8.866, -7.832, -6.73, -5.277, -3.192, -0.192},
                                {-12.186, -11.228, -10.256, -9.253, -8.186, -6.773, -4.732, -1.783},
                                {-13.498, -12.568, -11.624, -10.651, -9.618, -8.243, -6.247, -3.348},
                                {-14.789, -13.886, -12.969, -12.026, -11.025, -9.688, -7.735, -4.885},
                                {-16.058, -15.181, -14.292, -13.377, -12.409, -11.108, -9.196, -6.393},
                        },
                },
                {
                        {//100 MHz, 50%, Cold Sea, Fig4
                                {97.931, 102.263, 105.611, 106.74, 106.889, 106.9, 106.9, 106.9},
                                {88.379, 92.572, 96.98, 99.991, 100.79, 100.874, 100.879, 100.879},
                                {82.648, 86.625, 91.136, 95.249, 97.062, 97.338, 97.357, 97.358},
                                {78.482, 82.298, 86.746, 91.348, 94.193, 94.805, 94.856, 94.859},
                                {75.167, 78.869, 83.234, 88.032, 91.719, 92.805, 92.915, 92.92},
                                {72.384, 76.007, 80.3, 85.168, 89.471, 91.124, 91.325, 91.337},
                                {69.962, 73.534, 77.773, 82.658, 87.384, 89.645, 89.977, 89.997},
                                {67.801, 71.343, 75.545, 80.429, 85.436, 88.296, 88.803, 88.837},
                                {65.838, 69.367, 73.547, 78.425, 83.619, 87.035, 87.759, 87.813},
                                {64.03, 67.56, 71.73, 76.604, 81.923, 85.833, 86.816, 86.897},
                                {62.344, 65.888, 70.06, 74.933, 80.337, 84.675, 85.951, 86.067},
                                {60.761, 64.327, 68.511, 73.389, 78.853, 83.551, 85.149, 85.309},
                                {59.263, 62.86, 67.063, 71.952, 77.46, 82.457, 84.395, 84.61},
                                {57.838, 61.473, 65.702, 70.606, 76.15, 81.391, 83.679, 83.962},
                                {56.477, 60.155, 64.416, 69.339, 74.912, 80.354, 82.994, 83.357},
                                {55.171, 58.897, 63.196, 68.142, 73.74, 79.345, 82.332, 82.789},
                                {53.916, 57.694, 62.033, 67.005, 72.628, 78.365, 81.689, 82.253},
                                {52.706, 56.539, 60.921, 65.923, 71.567, 77.413, 81.06, 81.745},
                                {51.537, 55.428, 59.856, 64.888, 70.555, 76.489, 80.44, 81.262},
                                {50.408, 54.357, 58.832, 63.897, 69.585, 75.593, 79.829, 80.801},
                                {45.287, 49.527, 54.238, 59.471, 65.263, 71.492, 76.841, 78.73},
                                {40.877, 45.365, 50.281, 55.664, 61.548, 67.887, 73.918, 76.896},
                                {37.108, 41.759, 46.81, 52.295, 58.246, 64.653, 71.086, 75.152},
                                {33.912, 38.619, 43.719, 49.245, 55.23, 61.683, 68.371, 73.415},
                                {31.2, 35.863, 40.93, 46.436, 52.417, 58.9, 65.767, 71.643},
                                {28.941, 33.46, 38.41, 43.832, 49.77, 56.264, 63.272, 69.838},
                                {27.042, 31.343, 36.111, 41.395, 47.252, 53.737, 60.864, 68},
                                {25.433, 29.465, 34.002, 39.105, 44.846, 51.301, 58.526, 66.139},
                                {24.052, 27.784, 32.056, 36.944, 42.54, 48.941, 56.246, 64.264},
                                {22.844, 26.263, 30.251, 34.902, 40.327, 46.652, 54.017, 62.381},
                                {21.767, 24.873, 28.57, 32.968, 38.203, 44.431, 51.834, 60.496},
                                {20.784, 23.588, 26.996, 31.135, 36.165, 42.275, 49.695, 58.614},
                                {19.869, 22.389, 25.515, 29.395, 34.211, 40.186, 47.6, 56.739},
                                {19.003, 21.258, 24.117, 27.742, 32.338, 38.164, 45.55, 54.875},
                                {18.172, 20.185, 22.792, 26.169, 30.543, 36.209, 43.547, 53.027},
                                {17.365, 19.158, 21.531, 24.671, 28.825, 34.322, 41.594, 51.2},
                                {15.8, 17.22, 19.178, 21.879, 25.606, 30.748, 37.843, 47.624},
                                {14.28, 15.402, 17.014, 19.33, 22.658, 27.439, 34.308, 44.175},
                                {12.791, 13.681, 15.009, 16.994, 19.958, 24.386, 30.999, 40.875},
                                {11.332, 12.042, 13.142, 14.846, 17.486, 21.577, 27.915, 37.736},
                                {9.903, 10.476, 11.394, 12.865, 15.222, 19.001, 25.057, 34.763},
                                {8.508, 8.978, 9.753, 11.033, 13.149, 16.644, 22.418, 31.957},
                                {7.15, 7.542, 8.207, 9.335, 11.247, 14.49, 19.989, 29.316},
                                {5.831, 6.167, 6.748, 7.755, 9.5, 12.522, 17.76, 26.833},
                                {4.553, 4.847, 5.366, 6.28, 7.89, 10.724, 15.714, 24.502},
                                {3.315, 3.581, 4.055, 4.898, 6.4, 9.075, 13.837, 22.315},
                                {0.387, 0.618, 1.033, 1.775, 3.105, 5.488, 9.76, 17.415},
                                {-2.335, -2.106, -1.704, -0.995, 0.256, 2.462, 6.351, 13.209},
                                {-4.898, -4.656, -4.24, -3.527, -2.305, -0.208, 3.388, 9.555},
                                {-7.344, -7.079, -6.639, -5.906, -4.688, -2.661, 0.711, 6.321},
                                {-9.701, -9.411, -8.943, -8.186, -6.961, -4.981, -1.779, 3.4},
                                {-11.989, -11.675, -11.18, -10.399, -9.166, -7.218, -4.143, 0.712},
                                {-14.22, -13.885, -13.367, -12.565, -11.322, -9.398, -6.419, -1.806},
                                {-16.403, -16.05, -15.512, -14.692, -13.442, -11.535, -8.628, -4.194},
                                {-18.544, -18.176, -17.623, -16.788, -15.531, -13.637, -10.783, -6.484},
                                {-20.649, -20.269, -19.702, -18.856, -17.594, -15.709, -12.896, -8.698},
                                {-22.724, -22.334, -21.757, -20.902, -19.634, -17.757, -14.975, -10.853},
                                {-24.774, -24.377, -23.791, -22.928, -21.657, -19.785, -17.027, -12.964},
                                {-26.807, -26.403, -25.811, -24.942, -23.668, -21.799, -19.06, -15.042},
                                {-28.828, -28.419, -27.821, -26.947, -25.671, -23.805, -21.08, -17.098},
                                {-30.844, -30.43, -29.828, -28.95, -27.671, -25.808, -23.094, -19.14},
                                {-32.859, -32.442, -31.836, -30.955, -29.674, -27.813, -25.108, -21.175},
                                {-34.879, -34.459, -33.85, -32.966, -31.684, -29.824, -27.126, -23.211},
                                {-36.907, -36.485, -35.873, -34.988, -33.704, -31.846, -29.153, -25.253},
                                {-38.947, -38.523, -37.909, -37.021, -35.737, -33.879, -31.191, -27.302},
                                {-40.999, -40.573, -39.958, -39.069, -37.784, -35.926, -33.242, -29.363},
                                {-43.065, -42.638, -42.021, -41.131, -39.845, -37.988, -35.307, -31.435},
                                {-45.143, -44.715, -44.097, -43.206, -41.919, -40.063, -37.384, -33.519},
                                {-47.233, -46.804, -46.185, -45.292, -44.005, -42.149, -39.472, -35.613},
                                {-49.33, -48.9, -48.281, -47.387, -46.1, -44.244, -41.569, -37.714},
                                {-51.432, -51.002, -50.381, -49.487, -48.199, -46.344, -43.67, -39.819},
                                {-53.534, -53.103, -52.482, -51.587, -50.299, -48.443, -45.771, -41.923},
                                {-55.63, -55.198, -54.577, -53.682, -52.393, -50.538, -47.867, -44.021},
                                {-57.714, -57.282, -56.66, -55.764, -54.475, -52.62, -49.95, -46.107},
                                {-59.779, -59.347, -58.724, -57.829, -56.539, -54.685, -52.015, -48.174},
                                {-61.819, -61.386, -60.764, -59.868, -58.578, -56.723, -54.055, -50.215},
                                {-63.827, -63.393, -62.77, -61.874, -60.585, -58.73, -56.062, -52.224},
                                {-65.794, -65.36, -64.737, -63.841, -62.551, -60.696, -58.029, -54.192},
                        },
                        {//600 MHz, 50%, Cold Sea, Fig12
                                {106.891, 106.893, 106.9, 106.9, 106.9, 106.9, 106.9, 106.9},
                                {100.292, 100.769, 100.875, 100.878, 100.879, 100.879, 100.879, 100.879},
                                {94.553, 96.847, 97.317, 97.352, 97.357, 97.358, 97.358, 97.358},
                                {89.791, 93.521, 94.673, 94.842, 94.858, 94.859, 94.859, 94.859},
                                {86.015, 90.437, 92.41, 92.879, 92.918, 92.92, 92.921, 92.921},
                                {82.917, 87.598, 90.371, 91.252, 91.332, 91.337, 91.337, 91.337},
                                {80.294, 85.029, 88.492, 89.844, 89.988, 89.997, 89.998, 89.998},
                                {78.02, 82.718, 86.719, 88.58, 88.82, 88.837, 88.838, 88.838},
                                {76.012, 80.636, 85.027, 87.41, 87.784, 87.813, 87.815, 87.815},
                                {74.214, 78.746, 83.407, 86.299, 86.85, 86.897, 86.9, 86.9},
                                {72.583, 77.018, 81.857, 85.221, 85.995, 86.067, 86.072, 86.072},
                                {71.089, 75.427, 80.375, 84.158, 85.203, 85.308, 85.316, 85.316},
                                {69.709, 73.949, 78.959, 83.097, 84.459, 84.609, 84.62, 84.621},
                                {68.423, 72.567, 77.605, 82.033, 83.753, 83.96, 83.976, 83.977},
                                {67.215, 71.265, 76.309, 80.963, 83.074, 83.354, 83.376, 83.378},
                                {66.073, 70.032, 75.067, 79.887, 82.415, 82.784, 82.815, 82.817},
                                {64.986, 68.857, 73.873, 78.808, 81.768, 82.246, 82.288, 82.291},
                                {63.944, 67.731, 72.722, 77.731, 81.126, 81.734, 81.79, 81.794},
                                {62.94, 66.647, 71.611, 76.659, 80.486, 81.246, 81.319, 81.325},
                                {61.967, 65.599, 70.535, 75.595, 79.841, 80.778, 80.872, 80.879},
                                {57.403, 60.761, 65.559, 70.504, 76.457, 78.641, 78.916, 78.939},
                                {53.168, 56.424, 61.029, 65.866, 72.741, 76.642, 77.29, 77.352},
                                {49.251, 52.512, 56.763, 61.612, 68.827, 74.568, 75.867, 76.006},
                                {45.145, 48.561, 52.694, 57.611, 64.893, 72.283, 74.553, 74.831},
                                {41.133, 44.695, 48.804, 53.765, 61.031, 69.736, 73.273, 73.782},
                                {37.432, 41.035, 45.098, 50.031, 57.26, 66.951, 71.959, 72.824},
                                {34.071, 37.617, 41.58, 46.417, 53.572, 63.988, 70.556, 71.928},
                                {30.982, 34.42, 38.249, 42.945, 49.962, 60.901, 69.017, 71.069},
                                {28.088, 31.41, 35.101, 39.635, 46.44, 57.72, 67.316, 70.224},
                                {25.337, 28.558, 32.127, 36.496, 43.024, 54.458, 65.443, 69.37},
                                {22.694, 25.842, 29.314, 33.53, 39.731, 51.126, 63.402, 68.487},
                                {20.136, 23.247, 26.653, 30.733, 36.578, 47.752, 61.197, 67.552},
                                {17.776, 20.812, 24.132, 28.099, 33.574, 44.37, 58.826, 66.548},
                                {15.529, 18.498, 21.744, 25.622, 30.726, 41.025, 56.275, 65.46},
                                {13.368, 16.289, 19.482, 23.298, 28.038, 37.755, 53.529, 64.276},
                                {11.297, 14.186, 17.345, 21.12, 25.511, 34.591, 50.585, 62.988},
                                {7.516, 10.362, 13.473, 17.192, 20.947, 28.669, 44.242, 60.068},
                                {4.498, 7.207, 10.21, 13.799, 17.023, 23.396, 37.694, 56.587},
                                {3.004, 4.751, 7.543, 10.88, 13.692, 18.855, 31.387, 52.282},
                                {1.541, 2.748, 5.301, 8.352, 10.866, 15.065, 25.617, 46.965},
                                {0.103, 0.98, 3.329, 6.138, 8.444, 11.962, 20.58, 40.928},
                                {-1.311, -0.648, 1.548, 4.173, 6.335, 9.415, 16.395, 34.736},
                                {-2.702, -2.137, -0.081, 2.407, 4.468, 7.279, 13.055, 28.85},
                                {-4.07, -3.557, -1.588, 0.797, 2.787, 5.435, 10.422, 23.581},
                                {-5.417, -4.923, -2.996, -0.693, 1.242, 3.79, 8.295, 19.132},
                                {-6.741, -6.205, -4.334, -2.097, -0.205, 2.277, 6.494, 15.557},
                                {-9.955, -9.274, -7.514, -5.411, -3.598, -1.212, 2.673, 9.498},
                                {-13.033, -12.311, -10.642, -8.646, -6.893, -4.566, -0.827, 5.292},
                                {-15.981, -15.355, -13.766, -11.867, -10.166, -7.887, -4.247, 1.553},
                                {-18.809, -18.342, -16.824, -15.009, -13.353, -11.115, -7.554, -1.927},
                                {-21.529, -21.201, -19.747, -18.009, -16.394, -14.194, -10.702, -5.214},
                                {-24.151, -23.902, -22.507, -20.841, -19.263, -17.097, -13.669, -8.307},
                                {-26.687, -26.453, -25.115, -23.515, -21.971, -19.838, -16.47, -11.223},
                                {-29.15, -28.884, -27.597, -26.06, -24.548, -22.445, -19.133, -13.995},
                                {-31.55, -31.224, -29.987, -28.508, -27.027, -24.952, -21.692, -16.656},
                                {-33.896, -33.501, -32.31, -30.886, -29.434, -27.386, -24.175, -19.236},
                                {-36.198, -35.736, -34.588, -33.216, -31.791, -29.768, -26.604, -21.756},
                                {-38.464, -37.943, -36.836, -35.514, -34.114, -32.115, -28.996, -24.234},
                                {-40.7, -40.132, -39.065, -37.789, -36.414, -34.438, -31.361, -26.682},
                                {-42.911, -42.31, -41.28, -40.05, -38.698, -36.744, -33.707, -29.106},
                                {-45.104, -44.482, -43.488, -42.3, -40.97, -39.037, -36.039, -31.513},
                                {-47.281, -46.65, -45.69, -44.543, -43.235, -41.322, -38.36, -33.907},
                                {-49.445, -48.815, -47.889, -46.781, -45.493, -43.599, -40.673, -36.288},
                                {-51.598, -50.978, -50.083, -49.013, -47.745, -45.869, -42.977, -38.659},
                                {-53.743, -53.137, -52.273, -51.239, -49.99, -48.132, -45.24, -41.018},
                                {-55.878, -55.291, -54.455, -53.457, -52.226, -50.278, -47.39, -43.364},
                                {-58.005, -57.436, -56.629, -55.665, -54.451, -52.412, -49.529, -45.695},
                                {-60.123, -59.57, -58.79, -57.859, -56.662, -54.536, -51.657, -48.009},
                                {-62.23, -61.689, -60.936, -60.035, -58.855, -56.649, -53.774, -50.144},
                                {-64.325, -63.788, -63.061, -62.191, -60.962, -58.748, -55.877, -52.258},
                                {-66.405, -65.863, -65.161, -64.321, -63.045, -60.834, -57.966, -54.355},
                                {-68.469, -67.91, -67.231, -66.42, -65.112, -62.901, -60.037, -56.435},
                                {-70.514, -69.923, -69.268, -68.485, -67.159, -64.95, -62.088, -58.493},
                                {-72.46, -71.891, -71.265, -70.509, -69.183, -66.975, -64.116, -60.528},
                                {-74.355, -73.816, -73.219, -72.489, -71.182, -68.975, -66.118, -62.537},
                                {-76.205, -75.693, -75.124, -74.42, -73.152, -70.946, -68.091, -64.515},
                                {-78.004, -77.519, -76.977, -76.298, -75.09, -72.885, -70.031, -66.461},
                                {-79.748, -79.29, -78.773, -78.119, -76.993, -74.789, -71.937, -68.371},
                        },
                        {//2000 MHz, 50%, Cold Sea, Fig20
                                {106.896, 106.899, 106.9, 106.9, 106.9, 106.9, 106.9, 106.9},
                                {100.851, 100.871, 100.878, 100.879, 100.879, 100.879, 100.879, 100.879},
                                {97.265, 97.327, 97.35, 97.357, 97.358, 97.358, 97.358, 97.358},
                                {94.647, 94.782, 94.837, 94.856, 94.859, 94.859, 94.859, 94.859},
                                {92.52, 92.764, 92.873, 92.914, 92.921, 92.921, 92.921, 92.921},
                                {90.664, 91.059, 91.247, 91.323, 91.337, 91.337, 91.337, 91.337},
                                {88.961, 89.548, 89.844, 89.971, 89.998, 89.998, 89.998, 89.998},
                                {87.338, 88.156, 88.593, 88.792, 88.837, 88.838, 88.838, 88.838},
                                {85.752, 86.835, 87.445, 87.74, 87.814, 87.815, 87.815, 87.815},
                                {84.173, 85.55, 86.367, 86.785, 86.897, 86.9, 86.9, 86.9},
                                {82.585, 84.277, 85.333, 85.903, 86.068, 86.072, 86.072, 86.072},
                                {80.98, 82.998, 84.323, 85.075, 85.309, 85.316, 85.316, 85.316},
                                {79.357, 81.704, 83.321, 84.288, 84.61, 84.621, 84.621, 84.621},
                                {77.715, 80.387, 82.314, 83.528, 83.961, 83.977, 83.977, 83.977},
                                {76.06, 79.044, 81.294, 82.786, 83.354, 83.378, 83.378, 83.378},
                                {74.396, 77.675, 80.254, 82.052, 82.782, 82.817, 82.818, 82.818},
                                {72.73, 76.281, 79.189, 81.318, 82.241, 82.291, 82.291, 82.291},
                                {71.067, 74.866, 78.097, 80.578, 81.726, 81.794, 81.795, 81.795},
                                {69.414, 73.435, 76.976, 79.826, 81.232, 81.324, 81.325, 81.325},
                                {67.774, 71.991, 75.827, 79.056, 80.756, 80.879, 80.879, 80.879},
                                {59.906, 64.756, 69.741, 74.834, 78.512, 78.937, 78.941, 78.941},
                                {52.745, 57.81, 63.403, 69.926, 76.198, 77.339, 77.358, 77.358},
                                {46.318, 51.376, 57.195, 64.535, 73.432, 75.954, 76.019, 76.019},
                                {40.545, 45.497, 51.325, 58.984, 69.941, 74.668, 74.858, 74.859},
                                {35.334, 40.138, 45.867, 53.53, 65.684, 73.342, 73.834, 73.836},
                                {30.599, 35.242, 40.824, 48.314, 60.852, 71.785, 72.915, 72.921},
                                {26.267, 30.751, 36.168, 43.398, 55.727, 69.755, 72.076, 72.093},
                                {22.28, 26.613, 31.866, 38.797, 50.561, 67.015, 71.29, 71.337},
                                {18.596, 22.79, 27.884, 34.504, 45.52, 63.458, 70.52, 70.642},
                                {15.185, 19.25, 24.195, 30.507, 40.703, 59.174, 69.707, 69.998},
                                {12.036, 15.98, 20.782, 26.792, 36.16, 54.405, 68.752, 69.398},
                                {9.151, 12.975, 17.634, 23.348, 31.914, 49.421, 67.5, 68.836},
                                {6.55, 10.245, 14.75, 20.166, 27.978, 44.44, 65.743, 68.307},
                                {4.259, 7.807, 12.133, 17.241, 24.356, 39.611, 63.276, 67.803},
                                {2.99, 5.676, 9.787, 14.571, 21.046, 35.026, 60.002, 67.315},
                                {2.327, 3.855, 7.708, 12.149, 18.044, 30.74, 55.998, 66.829},
                                {1.061, 2.131, 4.298, 8.02, 12.92, 23.174, 46.736, 65.729},
                                {-0.158, 0.716, 1.934, 4.745, 8.854, 16.999, 37.327, 63.948},
                                {-1.356, -0.628, 0.443, 2.249, 5.659, 12.134, 28.841, 60.302},
                                {-2.55, -1.932, -0.972, 0.686, 3.187, 8.377, 21.701, 53.843},
                                {-3.746, -3.213, -2.338, -0.794, 1.548, 5.475, 15.978, 45.422},
                                {-4.95, -4.482, -3.674, -2.219, -0.002, 3.287, 11.535, 36.725},
                                {-6.161, -5.745, -4.989, -3.604, -1.486, 1.637, 8.111, 28.811},
                                {-7.38, -7.004, -6.29, -4.962, -2.923, 0.066, 5.425, 22.093},
                                {-8.604, -8.261, -7.581, -6.297, -4.322, -1.442, 3.237, 16.618},
                                {-9.831, -9.515, -8.861, -7.614, -5.692, -2.901, 1.369, 12.25},
                                {-12.895, -12.626, -12.02, -10.839, -9.011, -6.382, -2.565, 4.72},
                                {-15.923, -15.685, -15.11, -13.963, -12.195, -9.668, -6.003, -0.199},
                                {-18.888, -18.67, -18.046, -16.825, -15.117, -12.673, -9.163, -3.994},
                                {-21.771, -21.567, -20.743, -19.548, -17.891, -15.512, -12.103, -7.239},
                                {-24.562, -24.306, -23.308, -22.137, -20.523, -18.199, -14.866, -10.17},
                                {-27.26, -26.753, -25.77, -24.619, -23.043, -20.768, -17.498, -12.911},
                                {-29.866, -29.131, -28.161, -27.03, -25.488, -23.258, -20.042, -15.534},
                                {-32.253, -31.469, -30.512, -29.398, -27.888, -25.699, -22.532, -18.087},
                                {-34.564, -33.789, -32.843, -31.746, -30.266, -28.114, -24.993, -20.603},
                                {-36.871, -36.105, -35.171, -34.088, -32.635, -30.52, -27.441, -23.099},
                                {-39.183, -38.426, -37.502, -36.434, -35.007, -32.925, -29.886, -25.588},
                                {-41.505, -40.756, -39.842, -38.787, -37.384, -35.335, -32.333, -28.077},
                                {-43.838, -43.096, -42.191, -41.15, -39.77, -37.751, -34.785, -30.567},
                                {-46.181, -45.446, -44.55, -43.521, -42.163, -40.173, -37.241, -33.06},
                                {-48.435, -47.803, -46.915, -45.897, -44.561, -42.598, -39.698, -35.553},
                                {-50.617, -50.162, -49.281, -48.275, -46.958, -45.021, -42.044, -38.041},
                                {-52.796, -52.517, -51.644, -50.648, -49.351, -47.205, -44.233, -40.46},
                                {-54.976, -54.812, -53.997, -53.011, -51.654, -49.389, -46.422, -42.661},
                                {-57.162, -56.998, -56.331, -55.356, -53.841, -51.579, -48.614, -44.864},
                                {-59.354, -59.19, -58.641, -57.654, -56.035, -53.774, -50.812, -47.072},
                                {-61.552, -61.389, -60.89, -59.854, -58.236, -55.975, -53.016, -49.284},
                                {-63.756, -63.593, -63.094, -62.058, -60.44, -58.181, -55.224, -51.498},
                                {-65.96, -65.797, -65.298, -64.263, -62.646, -60.387, -57.433, -53.712},
                                {-68.159, -67.997, -67.468, -66.463, -64.846, -62.589, -59.636, -55.92},
                                {-70.347, -70.184, -69.534, -68.61, -67.035, -64.778, -61.826, -58.115},
                                {-72.513, -72.345, -71.529, -70.612, -69.202, -66.946, -63.995, -60.289},
                                {-74.649, -74.259, -73.447, -72.538, -71.339, -69.083, -66.134, -62.431},
                                {-76.744, -76.091, -75.284, -74.383, -73.256, -71.179, -68.23, -64.53},
                                {-78.494, -77.838, -77.036, -76.142, -75.028, -73.221, -70.274, -66.577},
                                {-80.149, -79.497, -78.7, -77.812, -76.711, -75.06, -72.254, -68.559},
                                {-81.714, -81.065, -80.273, -79.392, -78.303, -76.669, -74.153, -70.42},
                                {-83.188, -82.543, -81.756, -80.881, -79.804, -78.185, -75.687, -71.974},
                        },

                },
        };

    }

    ;



    public boolean limit(double var, double low, double hi, String name) {
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    21DEC16     Ivica Stevanovic, OFCOM         Initial version in Java

        boolean bool = false;

        if ((var < low) || (var > hi)) {
            bool = true;
            throw new RuntimeException(name + " = " + Double.toString(var) + " is outside the limits.");

        }
        return bool;
    }


    public double h1Calc(double d, double heff, double ha, double hb, int path, int flag) {
        // Input Variables
        // d     -   path length (km)
        // heff  -   effective height of the transmitting/base antenna, defined as
        //           its height over the average level of the ground between
        //           distances of 3 km and 15 km from the transmitting/base antenna
        //           in the direction of the receiving/mobile antenna (m)
        // ha    -   transmitting/base antenna height above ground (height of the
        //           mast) used when terrain information is not available (m)
        //           ha = -10000 if not defined by user
        // hb    -   transmitting/base antenna heightabove terrain height averaged
        //           between 0.2d and d (used when terrain information is available (m))
        //           hb = -10000 if not defined by user
        // path  -   type of the path (1 - 'Land' or 2 - 'Warm Sea' or 3- 'Cold Sea')
        // flag  -   = 1 (terrain information available), 0 (not available)
        //
        // Sec: 3 Determination of transmitting/base antenna height, h1
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    21DEC16     Ivica Stevanovic, OFCOM         Initial version in Java

        double h1;

        if (path == 1) { //Land
            if (d < 15) { // Section 3.1
                if (flag == 0) { // terrain info not available
                    if (d <= 3) {
                        if (ha >= -10000) {
                            h1 = ha;                    //eq'n (4)
                        } else {
                            //'d <= 3 km. No value for ha. Setting h1 = heff.');
                            h1 = heff;
                        }
                    } else { // 3 < d < 15
                        if (ha >= -10000) {
                            h1 = ha + (heff - ha) * (d - 3) / 12;     //equ'n (5)
                        } else {
                            //'3 km <d < 15 km. No value for ha. Setting h1 = heff.');
                            h1 = heff;
                        }
                    }
                } else { // terrain info available
                    if (hb > -10000) {
                        h1 = heff;                        //equ'n (6) if d < 15 m heff = hb
                    } else {
                        //('d < 15, terrain info available, No value for hb. Setting h1 = heff.')
                        h1 = heff;
                    }
                }
            } else { // d>15 (Section 3.2)
                h1 = heff;                          //equ'n (7)
            }
        } else { // path = 2 or 3 - Sea // Section 3.3
            if (heff < 3) {
                //'heff is too low for sea paths. Setting h1 = 3 m.');
                h1 = 3;
            }
            h1 = heff;
        }
        return h1;
    }

    public double step6_10(double t, double f, double h1, int path, double d, double Emax) {
        // step6_10(t, f, h1, path, d, Emax)
        //
        // where:
        //   t: percent time
        //   f: frequency
        //   h1: the calculated height of the reciving antenna
        //   path: 1 - Land, 2 - Warm Sea, 3 - Cold Sea
        //   d: distance
        //   Emax: maximum calculated field strength
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    21DEC16     Ivica Stevanovic, OFCOM         Initial version in Java

        double[] percentage = tabIndex[0];

        double[] tinfsup;
        tinfsup = searchclosest(percentage, t);
        double tinf = tinfsup[0];
        double tsup = tinfsup[1];
        int tinf_x = (int) tinfsup[2];
        int tsup_x = (int) tinfsup[3];


        // Step 6: For the lower nominal percentage time follow Steps 7 to 10.
        double[] Ep = new double[2];
        double E = 0;
        // Step 7-9: For the lower nominal frequency follow Steps 8 and 9.
        // Sec 6: Interpolation and extrapolation of field strength as a
        // function of frequency
        double df = D06(f, h1, 10);
        double d600 = D06(600, h1, 10);

        if ((path == 2 || path == 3) && (f < 100) && (d < d600)) { // Sea path
            if (d <= df) {
                Ep[0] = Step_19a(t, 0, d);      //equ'n (15a)
                Ep[1] = Ep[0];
            } else {
                double[] Ed600 = new double[2];

                double Edf = Step_19a(t, 0, df);
                Ed600[0] = step7_normal(tinf_x, f, h1, path, d, Emax);
                Ed600[1] = step7_normal(tsup_x, f, h1, path, d, Emax);
                Ep[0] = Edf + (Ed600[0] - Edf) * Math.log10(d / df) / Math.log10(d600 / df); //equ'n (15b)
                Ep[1] = Edf + (Ed600[1] - Edf) * Math.log10(d / df) / Math.log10(d600 / df); //equ'n (15b)
            }
        } else {
            Ep[0] = step7_normal(tinf_x, f, h1, path, d, Emax);
            Ep[1] = step7_normal(tsup_x, f, h1, path, d, Emax);
        }
        // Step 10: If the required percentage time does not coincide with the
        // lower nominal percentage time, repeat Steps 7 to 9 for the higher
        // nominal percentage time and interpolate the two field strengths using
        // the method given in Annex 5, § 7.

        // Sec 7: Interpolation of field strength as a function of percentage time

        if (tinf != tsup) {
            double Qsup = Qi(tsup / 100);
            double Qinf = Qi(tinf / 100);
            double Qt = Qi(t / 100);
            E = Ep[1] * (Qinf - Qt) / (Qinf - Qsup) + Ep[0] * (Qt - Qsup) / (Qinf - Qsup);    //equ'n (16)
        } else {
            E = Ep[0];
        }

        return E;
    }

    public double step7_normal(int t_x, double f, double h1, int path, double d, double Emax) {

        // E = step7_normal(t_x, f, h1, path, d, Emax)
        // Sec 6: Interpolation and extrapolation of field strength as a function of
        // frequency
        // where:
        // t_x - the index of percent time
        // f - frequency
        //   h1: is the caculated h1 from h1Calc
        //   path: 1 - Land, 2 - Warm Sea, 3 - Cold Sea
        //   d: is the distance
        //   Emax: the the max field strength
        // Step 7: For the lower nominal frequency follow Steps 8 and 9.
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    21DEC     Ivica Stevanovic, OFCOM         Initial version in Java


        double[] frequencies = tabIndex[1];

        // obatin fsup and finf

        double[] finfsup;
        finfsup = searchclosest(frequencies, f);
        double finf = finfsup[0];
        double fsup = finfsup[1];
        int finf_x = (int) finfsup[2];
        int fsup_x = (int) finfsup[3];


        // Step 8: Obtain the field strength exceeded at 50// locations for a
        // receiving/mobile antenna at the height of representative clutter, R,
        // above ground for the required distance and transmitting/base antenna
        // height as follows:
        double[] Ef = new double[2];

        if (h1 >= 10) {
            Ef[0] = step81(t_x, finf_x, h1, path, d, Emax);
            Ef[0] = Math.min(Ef[0], Emax);
            Ef[1] = step81(t_x, fsup_x, h1, path, d, Emax);
            Ef[1] = Math.min(Ef[1], Emax);

        } else {
            Ef[0] = step82(t_x, finf_x, h1, path, d, finf, f);
            Ef[1] = step82(t_x, fsup_x, h1, path, d, fsup, f);
        }

        double E = 0;
        if (finf != fsup) {
            E = Ef[0] + (Ef[1] - Ef[0]) * Math.log10(f / finf) / Math.log10(fsup / finf); //eq'n (14)
            if (f > 2000) {
                E = Math.min(E, Emax);
            }
        } else {
            E = Ef[0];
        }
        return E;
    }


    public double step81(int t_x, int f_x, double h1, int path, double d, double Emax) {
        // Step 8: Obtain the field strength exceeded at 50% locations for a
        // receiving/mobile antenna at the height of representative clutter, R,
        // above ground for the required distance and transmitting/base antenna
        // height as follows:
        // Step 8.1: For a transmitting/base antenna height h1 equal to or
        // greater than 10 m follow Steps 8.1.1 to 8.1.6:

        // E = step81(t_x, f_x, h1, path, d, Emax)
        //
        // Input arguments
        //  t_x - time percent index
        // f_x - frequency index
        // h1 is the caculated h1 from h1Calc
        // path = 1 (Land), 2 (Warm sea), 3 (Cold sea)
        // d - distance
        // Emax - maximum value of electric field strength
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    20DEC16     Ivica Stevanovic, OFCOM         Initial version


        // double check h1
        if (h1 < 10) {
            throw new RuntimeException("h1 is less than 10 m for step81.");
        }
        // obatin hsup and hinf

        double[] hinfsup;
        hinfsup = searchclosest(tabIndex[3], h1);
        double hinf = hinfsup[0];
        double hsup = hinfsup[1];
        int hinf_x = (int) hinfsup[2];
        int hsup_x = (int) hinfsup[3];

        double E = 0;

        double[] Eh1 = new double[2];

        Eh1[0] = step814_815(t_x, f_x, hinf_x, path, d);
        Eh1[1] = step814_815(t_x, f_x, hsup_x, path, d);

        if (hinf != hsup) {
            E = Eh1[0] + (Eh1[1] - Eh1[0]) * Math.log10(h1 / hinf) / Math.log10(hsup / hinf); //equ'n (8)
        } else {
            E = Eh1[0];
        }

        if (E > Emax) {
            E = Emax;
        }
        return E;
    }

    // 4 Application of transmitting/base antenna height, h1
    // The value of h1 controls which curve or curves are selected from which to
    // obtain field-strength values, and the interpolation or extrapolation
    // which may be necessary. The following cases are distinguished.

    // 4.1 Transmitting/base antenna height, h1, in the range 10 m to 3 000m
    // If the value of h1 coincides with one of the eight heights for which
    // curves are provided, namely 10, 20, 37.5, 75, 150, 300, 600 or 1 200m
    // the required field strength may be obtained directly from the plotted
    // curves or the associated tabulations. Otherwise the required field
    // strength should be interpolated or extrapolated from field strengths
    // obtained from two curves using:
    // E = Einf +(Esup ?Einf )log(h1 /hinf )/log(hsup /hinf ) dB(?V/m) (8)
    // where:
    // hinf : 10 m if h1 < 10 m, otherwise the nearest nominal effective
    // height below h1
    // hsup : 1 200 m if h1 > 1 200 m, otherwise the nearest nominal
    // effective height above h1
    // Einf : field-strength value for hinf at the required distance
    // Esup : field-strength value for hsup at the required distance.
    // The field strength resulting from extrapolation for h1 > 1 200 m
    // should be limited if necessary such that it does not exceed the
    // maximum defined in § 2.
    // This Recommendation is not valid for h1 > 3 000 m.


    public double step814_815(int t_x, int f_x, int h1_x, int path, double d) {
        //  Step 8.1.4: Obtain the ffield strength exceeded at 50%  locations
        //  for a receiving/mobile antenna at the height of representative
        //  clutter, R, for the required values of distance, d, and
        //  transmitting/base antenna height, h1[h1_x].
        //
        //  Step 8.1.5: If the required distance does not coincide with the
        //  lower nominal distance, repeat Step 8.1.4 for the higher nominal
        //  distance and interpolate the two field strengths for distance
        //  using the method given in Annex 5, § 5.
        //
        //  E = step814_815(t_x, f_x, h1_x, pathtype, dinf, dsup, d)
        //
        // Input arguments:
        //      t_x - index of time percent value
        //      f_x - index of frequency
        //      h1_x - index of transmitting antenna height
        //      path = 1 - Land, 2 - Warm (Sea), 3 - Cold (Sea)
        //      d - Tx-Rx distance
        //
        //  Rev   Date        Author                          Description
        // -------------------------------------------------------------------------------
        //  v1    20DEC16     Ivica Stevanovic, OFCOM         Initial version in Java


        double[] dinfsup;
        dinfsup = FindDNominals(d);
        double dinf = dinfsup[0];
        double dsup = dinfsup[1];
        int dinf_x = (int) dinfsup[2];
        int dsup_x = (int) dinfsup[3];
        double Esup;
        double Einf;
        if (path == 1) { // Land
            Esup = tabData.get[t_x][f_x][dsup_x][h1_x];
            Einf = tabData.get[t_x][f_x][dinf_x][h1_x];
        } else if (path == 2) { // Warm Sea
            Esup = tabDataSeaWarm.get[t_x][f_x][dsup_x][h1_x];
            Einf = tabDataSeaWarm.get[t_x][f_x][dinf_x][h1_x];
        } else { // Cold Sea
            Esup = tabDataSea.get[t_x][f_x][dsup_x][h1_x];
            Einf = tabDataSea.get[t_x][f_x][dinf_x][h1_x];
        }

        double E = 0;

        if (dinf != dsup) {
            E = Einf + (Esup - Einf) * Math.log10(d / dinf) / Math.log10(dsup / dinf);    // equ'n (13)
        } else {
            E = Einf;
        }
        return E;
    }


    public double step82(int t_x, int f_x, double h1, int path, double d, double fnom, double f) {
        //  Step 8.2: For a transmitting/base antenna height h1 less than 10 m
        //  determine the field strength for the required height and distance
        //  using the method given in Annex 5, § 4.2. If h1 is less than zero,
        //  the method given in Annex 5, § 4.3 should also be used.
        //
        //  E = step82(tabulatedValues,h1,dinf,dsup,d,path,fnom,f,t)
        //
        //  tabulatedValues defines a matrix of tabulated data
        //
        //  h1 - as calculated from h1Calc
        //  dinf and dsup must correspond to values in Table 1 of ITU-R P.1546
        //  d is the distance
        //  path either 'Land' or 'Sea'
        //  fnom the nominal frequency (100 600 or 1200 MHz)
        //  f frequency (needs to be checked if this is f or fnom)
        //
        //
        //  Note that 4.3a is not implemented
        //
        //  Rev   Date        Author                          Description
        // -------------------------------------------------------------------------------
        //  v1    20DEC16     Ivica Stevanovic, OFCOM         Initial version in Java

        double E = 0;
        if (h1 >= 10) {
            throw new RuntimeException("Incorrect h1 value for step82: Greater than 10 m");
        }
        // look up figure values for E10 and E20
        double E10 = step814_815(t_x, f_x, 0, path, d); // h1[0] = 10
        double E20 = step814_815(t_x, f_x, 1, path, d); // h1[1] = 20
        // // End lookup

        double v = V(fnom, -10);
        double Jval = 0;

        Jval = J(v);  //  equ'n (12a)

        double Ch1neg10 = 6.03 - Jval;                       //  equ'n (12)
        double C1020 = E10 - E20;                          //  equ'n (9b)
        double Ezero = E10 + 0.5 * (C1020 + Ch1neg10);       //  equ'n (9a)

        if (path == 1) { // Land path

            if (h1 >= 0) {

                E = Ezero + 0.1 * h1 * (E10 - Ezero);          //  equ'n (9)
                return E;
            } else { // h1 < 0

                v = V(fnom, h1);

                Jval = J(v);  //  equ'n (12a)

                E = Ezero + 6.03 - Jval;  //  equ'n (12)
                return E;
            }
        } else if (path == 2 || path == 3) { // path == 2, 3 (Warm or Cold Sea)
            if (h1 < 1) {
                throw new RuntimeException("h1 cannot be less than 1 m for calculating sea path");
            }
            double Dh1 = D06(fnom, h1, 10);             //  equ'n (10a)
            double D20 = D06(fnom, 20, 10);             //  equ'n (10b)
            if (d <= Dh1) {
                //E = Emaxvalue;                   //  equ'n (11a)
                double t = tabIndex[0][t_x];
                E = Step_19a(t, 0, d);
                return E;
            } else if ((d > Dh1) && (d < D20)) {

                double E10D20 = step814_815(t_x, f_x, 0, path, D20);
                double E20D20 = step814_815(t_x, f_x, 1, path, D20);
                double ED20 = E10D20 + (E20D20 - E10D20) * Math.log10(h1 / 10) / Math.log10(20 / 10);

                double t = tabIndex[0][t_x];

                double EDh1 = Step_19a(t, 0, Dh1);
                E = EDh1 + (ED20 - EDh1) * Math.log10(d / Dh1) / Math.log10(D20 / Dh1);
                return E;
            } else if (d >= D20) {
                double E1 = E10 + (E20 - E10) * Math.log10(h1 / 10) / Math.log(20 / 10);
                v = V(fnom, -10);
                Jval = J(v);

                Ch1neg10 = 6.03 - Jval;                          //  equ'n (12)
                C1020 = E10 - E20;                          //  equ'n (9b)
                Ezero = E10 + 0.5 * (C1020 + Ch1neg10);       //  equ'n (9a)
                double E2 = Ezero + 0.1 * h1 * (E10 - Ezero);          //  equ'n (9)
                double Fs = (d - D20) / d;
                E = E1 * (1 - Fs) + E2 * Fs;                        //  equ'n (11c)
                return E;
            } // end if d <= Dh1
        } else {


            new RuntimeException("No path selected in step82.");

        } // end if path land

        return E;

    }// //  end function


    public double Step_11a_rrc06(double[] Eland, double[] Esea, double[] dland, double[] dsea) {
        // E=Step_11a_rrc06(Eland, Esea, dland, dsea);
        // Step 11: If there are two or more different propagation zones which
        // involve at least one land/sea or land/costal land boundary, the following
        // method approved by RRC-06 shall be used (Section 8)
        // Input parameters
        // Eland  - a vector of field strengths. ith element is a field strength for
        //         land path i equal in length to the mixed path (dB(uV/m))
        // Esea   - a vector of field strengths. jth element is a field strength for
        //           sea-and-coastal-land path j equal in length to the mixed path
        //           (dB(uV/m))
        // dland  - a vector of land-path lengths: ith element is the length
        //          of a path in land zone i (km)
        // dsea   - a vector of sea-and-coastal-land path lengths: jth element is
        //          the length of a path in sea-and-coastal zone j (km).
        //
        // Rev   Date        Author                          Description
        //--------------------------------------------------------------------------
        // v1    21DEC16     Ivica Stevanovic, OFCOM         Initial version in Java

        // Verify that Eland and dland, Esea and dsea have the same length

        if (Eland.length != dland.length) {
            throw new RuntimeException("Vectors Eland and dland must be of the same length.");
        }

        if (Esea.length != dsea.length) {
            throw new RuntimeException("Vectors Esea and dsea must be of the same length.");
        }
        // Compute the mixed path interpolation factor

        double dlT = 0;
        double dsT = 0;

        for (int i = 0; i < dland.length; i++) {
            dlT = dlT + dland[i];
        }

        for (int i = 0; i < dsea.length; i++) {
            dsT = dsT + dsea[i];
        }

        double dtotal = dlT + dsT;

        double E = 0;


        if (dlT == 0) {

            // In case there is no land/sea or land/coastal-land transitions (meaning
            // that either dlT=0 or dsT=0) the following procedure is used

            for (int i = 0; i < dsea.length; i++) {
                E = E + dsea[i] * Esea[i];
            }
            E = E / dtotal;  // eqn (22)

        } else if (dsT == 0) {

            for (int i = 0; i < dland.length; i++) {
                E = E + dland[i] * Eland[i];
            }
            E = E / dtotal;  // eqn (22)

        } else {

            double Fsea = dsT / dtotal;
            double Edsea_sum = 0;
            double Edland_sum = 0;

            for (int i = 0; i < dsea.length; i++) {
                Edsea_sum = Edsea_sum + Esea[i] * dsea[i];
            }

            for (int i = 0; i < dland.length; i++) {
                Edland_sum = Edland_sum + Eland[i] * dland[i];
            }

            double Delta = Edsea_sum / dsT - Edland_sum / dlT;

            double V = Math.max(1.0, 1.0 + Delta / 40);

            double A0 = 1 - Math.pow(1 - Fsea, 2.0 / 3.0);

            double A = Math.pow(A0, V);

            E = (1 - A) * Edland_sum / dlT + A * Edsea_sum / dsT;  // eqn (23)

        }

        return E;
    }

    public double[] Step_12a(double f, double tca) {
        // [e, nu]=Step_12a(f,tca);
        // Step 12: If information on the terrain clearance angle at a
        // receiving/mobile antenna adjacent to land is available, correct the field
        // strength for terrain clearance angle at the receiver/mobile using the
        // method given in Annex 5, § 11 of ITU-R P.1546-6.
        // Input parameters
        // f - frequency (MHz)
        // tca - terrain clearance angle (deg) is the elevation angle of the line
        // from the receiving/mobile antenna which just clears all terrain
        // obstructions in the direction of the transmitter/base antenna over a
        // distance up to 16 km but not going beyond the transmitting/base antenna
        // The calculation of the tca angleshould no take Earth curvature into
        // account. and should be limited such that it is not less than 0.55 deg or
        // not more than +40 deg.
        //
        // Rev   Date        Author                          Description
        //--------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version

        double e = 0;

        if (tca > 40) {
            tca = 40;
        }

        if (tca < 0.55) {
            tca = 0.55;
        }


        double nup = 0.036 * Math.sqrt(f);
        double nu = 0.065 * tca * Math.sqrt(f);
        double J1 = 0;
        if (nup > -0.7806) {
            J1 = J(nup);
        }
        double J2 = 0;
        if (nu > -0.7806) {
            J2 = J(nu);
        }
        e = J1 - J2;

        double[] out = new double[2];

        out[0] = e;
        out[1] = nu;
        return out;
    }


    public double[] Step_13a(double d, double f, double t, double eff1, double eff2) {
        // [e,thetaS] = Step_13a(d,f,t,eff1,eff2)
        // Step 13: Calculate the estimated field strength due to tropospheric
        // scattering using the method given in Annex 5 § 13 of ITU-R P.1546-6 and,
        // if necessary, limit the final predicted field strength accordingly.
        // Input variables
        // d - path length (km)
        // f - required frequency (MHz)
        // t - required percentage of time (%)
        // eff1 - the h1 terminal's terrain clearance angle in degrees calculated
        //       using the method in Paragraph 4.3 case a, whether or not h1 is
        //       negative (degrees)
        // eff2 - the h2 terminal's clearance angel in degrees as calculated in
        //        Paragraph 11, noting that this is the elevation angle relative to
        //        the local horizontal (degrees)
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version


        double thetaS = 180 * d / Math.PI / 4 * 3 / 6370 + eff1 + eff2;       //equ'n (35)
        if (thetaS < 0) {
            thetaS = 0;
        }
        double Lf = 5 * Math.log10(f) - 2.5 * Math.pow(Math.log10(f) - 3.3, 2);    // (36a)
        double Gt = 10.1 * Math.pow(-Math.log10(0.02 * t), 0.7);                // (36b)
        double e = 24.4 - 20 * Math.log10(d) - 10 * thetaS - Lf + 0.15 * 325 + Gt;   // (36)

        double[] out = new double[2];
        out[0] = e;
        out[1] = thetaS;
        return out;
    }

    public double[] Step_14a(double h1, double d, double R2, double h2, double f, ClutterEnvironment area) {
        // [Correction, Rp] = Step_14a(h1,d,R,h2,f,area)
        // This function computes correction for receiving/mobile antenna height
        // according to Annex 5 Paragraph 9 of ITU-R P.1546-6
        // Input variables are
        // h1 - transmitting/base antenna height (m)
        // d  - receiving/mobile antenna distance (km)
        // R2  - height of the ground cover surrounding the receiving/mobile antenna,
        //      subject to a minimum height value of 10 m (m)
        //      Examples of reference heights are 15 m for an urban area, 20 m for
        //      a dense urban area and 10 m for a suburban area. For sea paths
        //      the notional value of R is 10 m.
        // h2 - receiving/mobile antenna height (m)
        // f  - frequency (MHz)
        // area - ClutterEnvironment.URBAN, DENSE_URBAN, RURAL, SUBURBAN, NONE, WATER
        //
        // This Recommendation (function) is not valid for receiving/mobile antenna
        // height, h2, less than 1 m when adjacent to land, or less than 3 m when
        // adjacent to sea
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version

        String path;


        if (area == ClutterEnvironment.URBAN || area == ClutterEnvironment.DENSE_URBAN || area == ClutterEnvironment.RURAL || area == ClutterEnvironment.SUBURBAN || area == ClutterEnvironment.NONE) {
            path = "Land";
        } else /*if (area==Sea)*/ {
            path = "Sea";
        }


        // This value will be used throughout the function:

        double K_h2 = 3.2 + 6.2 * Math.log10(f);

        // // R is subject to a minimum height value of 10 m
        // if (R2 < 10)
        //     R2 = 10;
        // end

        double Rp;
        double Correction;
        if (path.equals("Land")) {

            if (h2 < 1.0) {
                throw new RuntimeException("This Recommendation is not valid for receiving/mobile antenna height h2 < 1 m when adjacent to land.");
            }

            // if the receiving/mobile antenna is on land account should first be
            // taken of the elevation angle of the arriving ray by calculating a
            // modified representative clutter height Rp (m) given by:

            Rp = (1000 * d * R2 - 15 * h1) / (1000 * d - 15);

            // the value of Rp must be limited if necessary such that it is not less
            // than 1 m

            if (Rp < 1) {
                Rp = 1;
            }

            if (area == ClutterEnvironment.URBAN || area == ClutterEnvironment.DENSE_URBAN || area == ClutterEnvironment.SUBURBAN) {

                // When the receiving/mobile antenna is in an urban environment the
                // correction is then given by:

                if (h2 < Rp) {

                    double h_dif = Rp - h2;
                    double K_nu = 0.0108 * Math.sqrt(f);
                    double theta_clut = Math.atan(h_dif / 27) * 180 / Math.PI; //degrees

                    double nu = K_nu * Math.sqrt(h_dif * theta_clut);

                    Correction = 6.03 - J(nu); // (28a)

                } else {

                    Correction = K_h2 * Math.log10(h2 / Rp); //(28b)

                }

                if (Rp < 10) {
                    // In cases of an urban environment where Rp is less than 10 m,
                    // the correction given by equation (28a) or (28b) should be reduced by

                    Correction = Correction - K_h2 * Math.log10(10.0 / Rp);
                }
            } else {

                // When the receiving/mobile antenna is on land in a rural or open
                // environment, the correction is given by equation (28b) for all
                // values of h2 with Rp set to 10 m
                Rp = 10;
                Correction = K_h2 * Math.log10(h2 / 10);

            }
        } else {  // receiver adjacent to sea

            if (h2 < 3) {
                throw new RuntimeException("This recommendation is not valid for receiving/mobile antenna height h2 < 3 m when adjacent to sea.");
            }

            // In the following, the expression "adjacent to sea" applies to cases
            // where the receiving/mobile antenna is either over sea, or is
            // immediately adjacent to the sea, with no significant obstruction in
            // the direction of the transmitting/base station.

            if (h2 >= 10) {

                // Where the receiving/mobile antenna is adjacent to sea for h2>=10m,
                // the correction should be calculated using equation (28b) wih
                // Rp set to 10
                Rp = 10;
                Correction = K_h2 * Math.log10(h2 / 10);

            } else {

                // Where the receiving/mobile antenna is adjacent to sea for h2<10m,
                // an alternative method should be used, based upon the path lengths
                // at which 0.6 of the first Fresnel zone is just clear of
                // obstruction by the sea surface. An approximate method for
                // calculating this distance is given in Paragraph 18

                // distance at which the path just has 0.6 Fresnel clearance for h2
                // = 10 m calculated as D06(f, h1, 10):

                double d10 = D06(f, h1, 10);

                // Distance at which the path just has 0.6 Fresnel clearance for the
                // required value of h2 calculated as D06(f,h1,h2):
                double dh2 = D06(f, h1, h2);

                // Correction for the required value of h2 at distance d10 using
                // equation (27b) with Rp set to 10m
                Rp = 10;
                double C10 = K_h2 * Math.log10(h2 / 10);


                if (d >= d10) {

                    // If the required distance is equal to or greater than d10, then
                    // again the correction for the required value of h2 should be
                    // calculated using equation (28b) with Rp set to 10
                    Rp = 10;
                    Correction = C10;

                } else {

                    // if the required distance is less than d10, then the
                    // correction to be added to the field strength E should be
                    // calculated using

                    if (d <= dh2) {

                        Correction = 0;

                    } else { //  dh2 < d < d10

                        Correction = C10 * Math.log10(d / dh2) / Math.log10(d10 / dh2);

                    }

                    //

                }
            }

        }

        double[] out = new double[2];
        out[0] = Correction;
        out[1] = Rp;

        return out;
    }

    public double Step_15a(double ha, double R1, double f) {
        // Step 15: If there is clutter around the transmitting/base terminal, even
        // if at a lower height above ground than the antenna, correct for its
        // effect using method given in Annex 5, § 10 of ITU-R P.1546-6.
        // The correction is not applied to open/uncluttered transmitter
        //
        // This correction applies when the transmitting/base terminal is over or
        // adjacent to land on which there is clutter. The correction should be used
        // in all such cases, including when the antenna is above the clutter
        // height. The correction is zero when the terminal is higher than a
        // frequency-dependent clearance height above the clutter.
        // Input variables are
        // ha - transmitting/base terminal antenna height above ground (m) (i.e., height of the mast)
        // R  - representative of the height of the ground cover surrounding the
        //      transmitting/base antenna, subject to a minimum height value of 10 m (m)
        //      Examples of reference heights are 20 m for an urban area, 30 m for
        //      a dense urban area and 10 m for a suburban area. For sea paths
        //      the notional value of R is 10 m.
        // f  - frequency (MHz)
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version

        double K_nu = 0.0108 * Math.sqrt(f); // (30f)
        double hdif1 = ha - R1;              // (30d)
        double theta_clut = Math.atan(hdif1 / 27) * 180 / Math.PI; // degrees (30e)
        double nu;
        if (R1 >= ha) {
            nu = K_nu * Math.sqrt(hdif1 * theta_clut);
        } else {
            nu = -K_nu * Math.sqrt(hdif1 * theta_clut);
        }

        double Correction = 0;

        if (nu > -0.7806) {
            Correction = -J(nu);
        }
        return Correction;
    }

    public double J(double nu) {
        // outVal = J(nu)
        // This function computes the value of equation (12a)
        // according to Annex 5 Paragraph 4.3 of ITU-R P.1546-5
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version
        double outVal = 0;
        if (nu > -0.7806) {
            outVal = 6.9 + 20 * Math.log10(Math.sqrt(Math.pow(nu - 0.1, 2.0) + 1) + nu - 0.1);
        }
        return outVal;
    }


    public double Step_16a(double ha, double h2, double d, double htter, double hrter) {
        // Correction = Step_16a(ha, h2, d, htter, hrter)
        //
        // Step 16: A correction is required to take account of the difference in
        // height between the two antennas, according to Annex 5, Section 14
        // Input variables are
        // ha - transmitting/base terminal antenna height above ground (m)
        // h2 - receiving/mobile terminal antenna height above ground (m)
        // d  - horizontal path distance (km)
        // htter - terrain height in meters above sea level at the transmitter/base
        //         terminal (m)
        // hrter - terrain height in meters above sea level at the receiving/mobile
        //         terminal (m)
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version


        double d_slope = dslope(ha, h2, d, htter, hrter);

        double Correction = 20 * Math.log10(d / d_slope);

        return Correction;
    }

    public double Step_17a(double ha, double h2, double d, double Esup, double htter, double hrter) {
        // Correction = Step_17a(ha, h2, d, Esup, htter, hrter)
        // According to Annex 5, Section 15
        // Step 17: Extrapolation to distances less than 1 km
        // Input variables are
        // ha - transmitting/base terminal antenna height above ground (m)
        // h2 - receiving/mobile terminal antenna height above ground (m)
        // d  - horizontal path distance (km)
        // Esup - the field strength given computed by steps 1-16 of P.1546-5
        //        (dB(uV/m))
        // htter - terrain height in meters above sea level at the transmitter/base
        //         terminal (m)
        // hrter - terrain height in meters above sea level at the receiving/mobile
        //         terminal (m)
        // Note: the extension to arbitrarily short horizontal distance is based on
        // the assumption that as a path decreases in length below 1 km there is an
        // increasing probability that a lower-loss path will exist passing around
        // obstacles rather than over them. For paths of 0.04 km horizontal distance
        // or shorter it is assumed that line-of-sight with full Fresnel clearance
        // esists between the terminals, and the field strength is calulated as the
        // free-space value based on slope distance.
        // If these assumptions do not fit the required short-range scenario,
        // appropriate adjustments should be made to account for effects such as
        // street-canyon propagation, building entry, indoor sections of path, or
        // body effects.
        // This extension to short distances can allow the path to have a steep
        // inclination, or even be vertical if ha > h2. It is important to note that
        // the predicted field strength does not take into account of the vertical
        // radiation pattern of the transmitting/base antenna. The field strength
        // corresponds to 1 kW e.r.p. in the direction of radiation.
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version


        double d_slope = dslope(ha, h2, d, htter, hrter);

        double dinf = 0.04;
        double dsup = 1;

        // For paths less than 1 km the model is extended to arbitrarily short
        // horizontal distances as follows.

        // If the horizontal distance is less than or equal to 0.04 km, the field
        // strength E is given by:
        double E;
        if (d <= dinf) {
            E = 106.9 - 20 * Math.log10(d_slope);
        } else {
            double dinf_slope = dslope(ha, h2, dinf, htter, hrter);
            double dsup_slope = dslope(ha, h2, dsup, htter, hrter);
            double Einf = 106.9 - 20 * Math.log10(dinf_slope);
            E = Einf + (Esup - Einf) * Math.log10(d_slope / dinf_slope) / Math.log10(dsup_slope / dinf_slope);
        }
        return E;
    }


    public double Step_18a(double Emedian, double q, double sigma_L) {
        // E = Step_18a(Emedian, f, sigma_L)
        //
        // Step 18: If the field strength at a receiving/mobile antenna adjacent to
        // land exceeded at percentage locations other than 50% is required, correct
        // the field strength for the required percentage of locations using the
        // method given in Annex 5, Paragraph 12
        // Input variables are
        // Emedian  - field strength exceeded for 50% of locations (as computed in
        //            steps 1-17 (dB(uV/m))
        // q  - percentage location (between 1% and 99%)
        //
        // NOTE: The location variability correction is NOT applied when the
        //       receiver/mobile is adjacent to sea.
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version
        // v2    20JAN17     Ivica Stevanovic, OFCOM         Modified for sigma_L

        if (q < 1 || q > 99) {
            throw new RuntimeException("The percentage location out of band [1%, 99%].");
        }


        //System.out.printf("%s\n", P1546ver5Input.generalEnvironment);
        //System.out.printf("%s\n", P1546ver5Input.System);


        double E = Emedian + Qi(q / 100) * sigma_L;

        return E;
    }

    public double Step_19a(double t, double dland, double dsea) {
        //  E = Step_19a(t, dland, dsea)
        //
        //  Step 19: If necessary, limit the resulting field strength to the maximum
        //  given in Annex 5, Paragraph 2. If a mixed path calculation has been made
        //  for a percentage time less than 50%  it will be necessary to calculate the
        //  maximum field strength by linear interpolation between the all-land and
        //  all-sea values.
        //  Input variables are
        //  t       - percentage time (%)
        //  dland   - total distance of land paths (km)
        //  dsea    - total distance of sea paths (km)
        //
        //  Rev   Date        Author                          Description
        // --------------------------------------------------------------------------
        //  v1    19DEC16     Ivica Stevanovic, OFCOM         Initial version in Java

        if (t < 1 || t > 50) {
            throw new RuntimeException("The percentage time out of band [1% , 50% ].");
        }

        double dtotal = dland + dsea;

        double Efs = 106.9 - 20 * Math.log10(dtotal); //  (2)
        double Ese = 2.38 * (1 - Math.exp(-dtotal / 8.94)) * Math.log10(50 / t); // (3)

        //  Linearly interpolate between all-land and all-sea values:

        double Emax = Efs + dsea * Ese / dtotal;  // (42)

        return Emax;

    }

    public double Step_20a(double f, double E) {
        // Lb = Step_20a(f,E)
        //
        // Step 20: If required, convert field strength to equivalent
        // basic transmission loss for the path using the method given in Annex 5,
        // Paragraph 16
        // Input variables are
        // f       - required frequency (MHz)
        // E       - field strength (dB(uV/m)) for 1 kW of e.r.p.
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version


        double Lb = 139.3 - E + 20 * Math.log10(f);

        return Lb;
    }

    public double dslope(double ha, double h2, double d, double htter, double hrter) {

        // outVal = dslope(ha, h2, d, htter, hrter)
        //
        //
        // This function computes slope distance as given by equations (37a,b) in ITU-R
        // P.1546-5
        // Input variables are
        // ha - transmitting/base terminal antenna height above ground (m)
        // h2 - receiving/mobile terminal antenna height above ground (m)
        // d  - horizontal path distance (km)
        // htter - terrain height in meters above sea level at the transmitter/base
        //         terminal (m)
        // hrter - terrain height in meters above sea level at the receiving/mobile
        //         terminal (m)
        //
        // Rev   Date        Author                          Description
        //--------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version


        double outVal;

        outVal = Math.sqrt(d * d + 1e-6 * Math.pow((ha + htter) - (h2 + hrter), 2.0));

        return outVal;
    }


    public double Qi(double x) {
        //Anex 5, Sec. 16 An approximation to the inverse complementary cumulative normal distribution
        // function
        // Rev     Date    Author                      Description
        // -------------------------------------------------------------------------------
        // v1      1DEC16  Ivica Stevanovic, OFCOM     Initial version

        double out;

        if (x <= .5) {
            out = T(x) - C(x);          //(39 a)
        } else {
            out = -(T(1 - x) - C(1 - x)); //(39 b)
        }

        return out;

    }

    public double T(double y) {
        double outT = Math.sqrt(-2 * Math.log(y));     //(39 c)
        return outT;
    }

    public double C(double z) {
        double C0 = 2.515517;
        double C1 = 0.802853;
        double C2 = 0.010328;
        double D1 = 1.432788;
        double D2 = 0.189269;
        double D3 = 0.001308;
        double outC = (((C2 * T(z) + C1) * T(z)) + C0) / (((D3 * T(z) + D2) * T(z) + D1) * T(z) + 1);//(39d)
        return outC;
    }

    public double D06(double f, double h1, double h2) {
        // Sec: 18 An approximation to the 0.6 Fresnel clearance path length
        // The path length which just achieves a clearance of 0.6 of the first
        // Fresnel zone over a smooth curved Earth, for a given frequency and
        // antenna heights h1 and h2
        // f : frequency (MHz)
        // h1, h2 : antenna heights above smooth Earth (m).
        // The value of h1 must be limited, if necessary,
        // such that it is not less than zero. Moreover, the resulting values of D06
        // must be limited, if necessary, such that it is not less than 0.001 km.
        //
        // Rev   Date        Author                          Description
        //-------------------------------------------------------------------------------
        // v1    01DEC16     Ivica Stevanovic, OFCOM         Initial version

        if (h1 < 0) {
            h1 = 0;
        }
        double Df = 0.0000389 * f * h1 * h2;             // equ'n (41a)
        double Dh = 4.1 * (Math.sqrt(h1) + Math.sqrt(h2));       // equ'n (41b)
        double D = Df * Dh / (Df + Dh);                  // equ'n (41)
        if (D < 0.001) {
            D = 0.001;
        }
        return D;
    }

    public double V(double Kv, double h1) {
        // deg = V(Kv,h1)
        // Calculates V for Annex 5 section 4.3 case b
        // error if input for Kv is not a nominal frequency.
        //
        // Rev   Date        Author                          Description
        // -------------------------------------------------------------------------------
        // v1    19DEC16     Ivica Stevanovic, OFCOM         Initial version in Java

        double deg = 0.0;

        if (Kv == 100) {
            deg = 1.35 * Math.atan(-h1 / 9000) * 180 / Math.PI; // equ'n (12c and 12b)
        } else if (Kv == 600) {
            deg = 3.31 * Math.atan(-h1 / 9000) * 180 / Math.PI; // equ'n (12c and 12b)
        } else if (Kv == 2000) {
            deg = 6 * Math.atan(-h1 / 9000) * 180 / Math.PI;    // equ'n (12c and 12b)
        } else {

            throw new RuntimeException("Invalid frequency input.");
        }

        return deg;
    }


    public double[] FindDNominals(double d) {
        // This function finds two closest values of distances in the tables dinf, dsup, to the distance d
        // so that dinf <= d <= dsup, the function also returns the indices of dinf_x and dsup_x

        //  Rev   Date        Author                          Description
        // -------------------------------------------------------------------------------
        //  v1    19DEC16     Ivica Stevanovic, OFCOM         Initial version in Java


        double[] distance = tabIndex[2]; // distances from the excel tables

        double[] dinfsup = searchclosest(distance, d);

        return dinfsup;
    }

    public double[] searchclosest(double[] x, double v) {
        // //
        // The following code tidbit is by Dr. Murtaza Khan, modified to return
        // vector y instead of index i if no exact value found. Also added
        // functionality for when v is outside of vector x min and max.
        // 23 May 2007 (Updated 09 Jul 2009)
        //  Obtained from http://www.mathworks.com/matlabcentral/fileexchange/15088
        // Begin
        //  //  Algorithm
        //  //  First binary search is used to find v in x. If not found
        //  //  then range obtained by binary search is searched linearly
        //  //  to find the closest value.
        //  //
        //  //  INPUT:
        //  //  x: vector of numeric values,
        //  //  x should already be sorted in ascending order
        //  //     (e.g. 2,7,20,...120)
        //  //  v: numeric value to be search in x
        //  //
        //  //  OUTPUT:
        //  //  i: lower or equal value to v in x
        //  //  cv: value that is equal or higher to v in x
        //      i_x: index of the value i
        //      cv_x: index of the value cv
        //
        // Rev   Date        Author                          Description
        // -------------------------------------------------------------------------------
        // v1    19DEC16     Ivica Stevanovic, OFCOM         Initial version in Java

        int end = x.length - 1;
        double i;
        double cv;
        double[] out = new double[4];
        double cv_x;
        double i_x;

        if (x[end] < v) {
            cv = x[end];
            cv_x = end;
            i = x[end - 1];
            i_x = end - 1;
            out[3] = cv_x;
            out[2] = i_x;
            out[1] = cv;
            out[0] = i;
            return out;
        } else if (x[0] > v) {

            cv = x[1];
            cv_x = 1;
            i = x[0];
            i_x = 0;
            out[3] = cv_x;
            out[2] = i_x;
            out[1] = cv;
            out[0] = i;
            return out;
        }

        int from = 0;
        int to = end;

        //  //  Phase 1: Binary Search
        while (from <= to) {
            int mid = Math.round((from + to) / 2);
            double diff = x[mid] - v;
            if (diff == 0) {
                i = v;
                cv = v;
                out[3] = mid;
                out[2] = mid;
                out[1] = cv;
                out[0] = i;
                return out;
            } else if (diff < 0) {     //  x(mid) < v
                from = mid + 1;
            } else {              //  x(mid) > v
                to = mid - 1;
            }
        }

        cv = x[to];
        cv_x = to;
        i = x[from];
        i_x = from;
        out[3] = cv_x;
        out[2] = i_x;
        out[1] = cv;
        out[0] = i;
        return out;

        //  //  //  --------------------------------
        //  //  //  Author: Dr. Murtaza Khan
        //  //  //  Email : drkhanmurtaza@gmail.com
        //  //  //  --------------------------------
    }
    // End tidbit


}
