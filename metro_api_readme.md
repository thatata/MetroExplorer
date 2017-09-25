# API Information
Username: thatata@gwmail.gwu.edu
PW: tarbi_is_awesome

API Key: e825c39a57db43a7a1b23206529caab4
Secondary API Key: 25079c978ed845838005e71d04c57f09

# Useful Methods
## Rail Station Information
1. Path between Stations -- return a set of ordered stations and distances between them on the SAME LINE
	https://api.wmata.com/Rail.svc/json/jPath[?FromStationCode][&ToStationCode]
2. Station Information -- return station location and address information based on a given stationCode
	https://api.wmata.com/Rail.svc/json/jStationInfo[?StationCode]
3. Station List -- return a list of station locations and address information based on a given lineCode
	https://api.wmata.com/Rail.svc/json/jStations[?LineCode]
4. Station to Station Information -- return a distance, fare information, and estimated travel time between any two stations, including those on different lines
	https://api.wmata.com/Rail.svc/json/jSrcStationToDstStationInfo[?FromStationCode][&ToStationCode]
