# postchi

A simple service to provide operations for postal codes

## Usage

### Initialize

To use postchi, there should be a source from which post codes can be loaded. By default, postchi looks for a system property or an environment variable with name `postchi.source`. It can be provided through a system property as in:

```
JAVA_COMMANDS -Dpostchi.source=/path/to/source
```

or exposed through an environment variable:

```
export postchi.source=/path/to/source
```

### Source Formats

TBD

### Through API

TBD

### Through HTTP

postchi can bind himself to the default address `/postchi` by default. It then exposes a REST service with the a set of resources. **Note** that all resources of postchi are read-only.

The main resource of postchi is postal code located at `/zipcode/[CODE]` in which `[CODE]` is a postal code. If the postal code is not valid in the usage, postchi returns an HTTP 404.

To obtain different properties of a postal code, the following properties are retrievable through the resource:

* `/zipcode/[CODE]/street` returns the street for the postal code
* `/zipcode/[CODE]/region` returns the logical region of the postal code based on the definition of the country; i.e. it could be a state or province by definition
* `/zipcode[CODE]/city` return the city for the postal code



