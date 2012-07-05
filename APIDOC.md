# AgMIP ACE REST API Documentation #

## Dataset Resources ##
A dataset is a full experiment will associated weather and soils

__POST__ ```/datasets/``` (_JSON_)

Creates a new dataset based on the JSON string passed to it. This should be a
complete experiment based on the ICASA standards, under a ```data``` variable.
```{"data":"{"exp_name":...}}```. 
Returns a new _id_, if successful.

__GET__ ```/datasets/:id```

Returns the dataset associated with _id_.

__DELETE__ ```/datasets/:id```

Deletes the dataset associated with _id_.


## Metadata Resources ##
Metadata associated with a dataset.

__GET__ ```/metadata/:id```

Returns the metadata for dataset _id_.

## Query Resources ##
The query resources perform the searches against metadata.

__GET__ ```/query/?<var>=<value>```

Returns the metadata associated with the id's that match the query. _var_ is the ICASA variable.

## Cache Resources ##
Cache resources are used to store commonly accessed information in a quick fashion.

__GET__ ```/cache/map```

Returns a list of all points currently on the map with associated keys in the following format.
```[{"lat":"12.34", "lon":"12.34", "key":"abc123"},...]```

__GET__ ```/cache/countries```

Returns a list of all unique ```FL_LOC_1``` values.
```["USA", "GHANA", "KENYA", "INDIA",...]```

__GET__ ```/cache/crops```

Returns a list of all unique ```CRID``` values.
```["MAZ","BWH",...]```

