# Building the Caches
The concept behind building the caches is simple, we create
a Collection of Cache interfaced objects in our Service
and then send them to the resource to handle the QUERY of
those caches (exposed via REST interface).

The Cache interface should define a path (?? are @Path
objects able to be dynamic or not in Jersey) and then expose
ways to extract the data from the database. If caches need
to be managed, we should attempt to handle that in one manager
(?? CacheManager ??) and then deal with the timings. (eg. collecting
various timestamped objects and merging them into the main data entry
to be returned). 

All of the "data" should be kept at a higher level (probably the Metadata level)
