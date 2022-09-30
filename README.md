# Traffic Parser

## How it works:
The XML files are parsed using [SAX Parser](https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html).

The GPS values are fed to a locally running [Nominatim](https://nominatim.org/) server for reverse look ups.
The nominatim server only has data from Utrecht coordinates and therefore will return a valid value if the 
coordinates of the site are in Utrecht. The docker compose file should wait a given amount for the nominatim
server to come online (on initial set up). This can be configured in the the compose file (by setting the time out value
of the `wait-for-it.sh` script).
 

With this data in hand, the values are (batch) inserted into three tables (see tables.pdf).

Upon a GET request, this data is queries and served. The json value is psuedo cached for 10 minutes.

On a 8 core machine, the Nominatim (one time) setup takes ~10 minutes and the querying ~12 minutes.
On 4 VCPU the nominatim initial setup and querying both take ~25 minutes each.
