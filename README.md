# Java Implementation of Recommendation ITU-R P.1546

This code repository contains a Java software implementation of  [Recommendation ITU-R P.1546-6](https://www.itu.int/rec/R-REC-P.1546/en)  with a method for point-to-area predictions for terrestrial services in the frequency range 30 MHz to 4000 MHz. 

This version of the code is functionally identical to the reference version approved by ITU-R Working Party 3K and published by Study Group 3 on [ITU-R SG 3 Software, Data, and Validation Web Page](https://www.itu.int/en/ITU-R/study-groups/rsg3/Pages/iono-tropo-spheric.aspx). This version of the code is also implemented in [SEAMCAT](https://seamcat.org).


The following table describes the structure of the folder `./src/` containing the Java implementation of Recommendation ITU-R P.1546.

| File/Folder               | Description                                                         |
|----------------------------|---------------------------------------------------------------------|
|`main/P1546.java`                | Java class implementing Recommendation ITU-R P.1546-6         |
|`test/P1546Test.java`          | Java class implementing validation tests against the reference MATLAB/Octave implementation of this Recommendation for a range of input variables.          |



## Function Call

~~~ 
Lb = P1546FieldStrMixed(f, t, heff, h2, R2, area, d_v, path_c, pathinfo, q, PTx, ha, hb, R1, tca, htter, hrter, eff1, eff2, sigmaL);
~~~

| Variable          | Type   | Units | Limits       | Description  |
|-------------------|--------|-------|--------------|--------------|
| `f`               | scalar double | MHz   | 30 ≤ `f` ≤ 4000 | Frequency   |
| `t         `      | scalar double | %     | 1 ≤ `p` ≤ 50 | Time percentage for which the calculated basic transmission loss is not exceeded |
| `heff`          | scalar double | m    |   | Effective height of the transmitting/base antenna, height over the average level of the ground between distances of 3 and 15 km from the transmitting/base antenna in the direction of the receiving/mobile antenna.|
| `h2`           | scalar double    | m      |             |  Receiving/mobile antenna height above ground level |
| `R2`           | scalar double    | m      |              |  Representative clutter height around receiver. Typical values: `R2`=10 for `area`=RURAL or SUBURBAN or WATER,  `R2`=15 for `area`=URBAN, `R2`=20 for `area`=DENSE_URBAN    |
| `area`           | P1546.ClutterEnvironment    |       | RURAL, SUBURBAN, URBAN, DENSE_URBAN, WATER, NONE           |  Area around the receiver.|
| `d_v`               | array double | km    | `sum(d_v)` ≤ ~1000 | Array of horizontal path lengths over different path zones starting from transmitter/base station terminal.|
| `path_c`           | array String    |       |     'Land', 'Sea', 'Warm', 'Cold'         |  Array of strings defining the path zone for each given path lenght in `d_v` starting from the transmitter/base terminal. |
| `pathinfo`           | scalar int    |      |        0, 1    |  0 - no terrain profile information available, 1 - terrain information available |
| `q`           | scalar double    | %      |   1 ≤ `q`  ≤ 99          |  Location percentage for which the calculated basic transmission loss is not exceeded. Default is 50%. |
| `wa`           | scalar double    | m      |   ~50 ≤ wa ≤ ~1000         |  The width of the square area over which the variabilitiy applies. Needs to be defined only if `pathinfo`= 1 and `q` ≠ 50. Default: 0 dB. |
| `Ptx`           | scalar double    | kW      |   `Ptx` > 0          |  Tx power; Default: 1. |
| `ha`           | scalar double    | m      |             |  Transmitter antenna height above ground. Defined in Annex 5 §3.1.1. Limits are defined in Annex 5 §3. |
| `hb`           | scalar double    | m      |             |  Height of transmitter/base antenna above terrain height averaged over 0.2d and d, when d is less than 15 km and where terrain information is available. |
| `R1`           | scalar double    | m      |              |  Representative clutter height around transmitter.   |
| `tca`           | scalar double    | deg      | 0.55   ≤  `tca`  ≤ 40        |  Terrain clearance angle.|
| `htter`           | scalar double    | m      |         | Terrain height in meters above sea level at the transmitter/base.|
| `hrter`           | scalar double    | m      |         | Terrain height in meters above sea level at the receiver/mobile.|
| `eff1`           | scalar double    | deg      |        |  The h1 terminal's terrain clearance angle calculated using the method in §4.3 case a), whether or not h1 is negative.|
| `eff2`           | scalar double    | deg      |        |  The h2 terminal's terrain clearance angle calculated using the method in §11, noting that this is the elevation angle relative to the local horizontal.|
| `sigmaL`           | scalar double    |   dB    |  non-negative           |  Standard deviation of the Gauss distribution of the local means in the study area (Annex 5, Section 12). |

 
## Outputs ##

| Variable   | Type   | Units | Description |
|------------|--------|-------|-------------|
| `Lb`    | double | dB    | Basic transmission loss |

## Notes

If sea path is selected for a `t` value less then 50% the default 10% table use is a cold sea path.

Not implemented in this version of the code:
* Annex 7: Adjustment for different climatic regions
* Annex 5, Section 4.3a): C_h1 calculation (terrain database is  available and the potential of discontinuities around h1 = 0 is of no concern)




## References

* [Recommendation ITU-R P.1546](https://www.itu.int/rec/R-REC-P.1546/en)

* [ITU-R SG 3 Software, Data, and Validation Web Page](https://www.itu.int/en/ITU-R/study-groups/rsg3/Pages/iono-tropo-spheric.aspx)

* [MATLAB/Octave Implementation of Recommendation ITU-R P.1546](https://github/eeveetza/p1546)

* [SEAMCAT - Spectrum Engineering Advanced Monte Carlo Analysis Tool](https://seamcat.org)