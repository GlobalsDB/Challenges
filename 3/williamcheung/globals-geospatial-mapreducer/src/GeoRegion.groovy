class GeoRegion {

    private static final MAX_LATITUDE = 90
    private static final MAX_LONGITUDE = 180

    int latitudeUp
    int latitudeDown
    int longitudeLeft
    int longitudeRight

    /**
    * private constructor for use by public factory method
    */
    private GeoRegion(int latitudeUp, int latitudeDown, int longitudeLeft, int longitudeRight) {
        this.latitudeUp = latitudeUp
        this.latitudeDown = latitudeDown
        this.longitudeLeft = longitudeLeft
        this.longitudeRight = longitudeRight
    }

    /**
     * factory method
     */
    static GeoRegion computeRegionAroundLatLon(double latitude, double longitude, int latitudeSpan, int longitudeSpan) {
        assert Math.abs(latitude)  <= MAX_LATITUDE
        assert Math.abs(longitude) <= MAX_LONGITUDE

        def positiveLatitude  = latitude  >= 0
        def positiveLongitude = longitude >= 0

        def latitudeBoundary  = ((int) (Math.abs(latitude)  / latitudeSpan))  * latitudeSpan
        def longitudeBoundary = ((int) (Math.abs(longitude) / longitudeSpan)) * longitudeSpan

        if (latitudeBoundary >= MAX_LATITUDE)
            latitudeBoundary = MAX_LATITUDE - latitudeSpan
        if (longitudeBoundary >= MAX_LONGITUDE)
            longitudeBoundary = MAX_LONGITUDE - longitudeSpan

        def latitudeUp = positiveLatitude ? latitudeBoundary + latitudeSpan : -1*latitudeBoundary
        def longitudeRight = positiveLongitude ? longitudeBoundary + longitudeSpan : -1*longitudeBoundary

        def latitudeDown = latitudeUp - latitudeSpan
        def longitudeLeft = longitudeRight - longitudeSpan

        new GeoRegion(latitudeUp, latitudeDown, longitudeLeft, longitudeRight)
    }
}
