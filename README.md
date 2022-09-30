# Traffic Parser

## How it works:
The XML files are parsed using [https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html](SAX Parser).

The GPS values are fed to a locally running [https://nominatim.org/](Nominatim) server for reverse look ups.
The nominatim server only has data from Utrecht coordinates and therefore will return a valid value if the 
coordinates of the site are in Utrecht.

With this data in hand, the values are (batch) inserted into three tables (see tables.pdf).

Upon a GET request, this data is queries and served. The json value is psuedo cached for 10 minutes.

On a 8 core machine, the Nominatim (one time) setup takes ~10 minutes and the querying ~12 minutes.
On 4 VCPU the nominatim initial setup takes ~25 minutes and the querying.
