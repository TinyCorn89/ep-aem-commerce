Elastic Path Commerce Implementation
====================================

Key Classes
------------
+ `CortexContext` encapsulates cortex's authentication context. To use any of the resources on the cortex api you must have a context. Cortex
has two allowable roles 'PUBLIC' or 'REGISTERED' and OTB only accepts grant_type 'password'.
+ `AemCortexContextManager` manages the persistence and retrieval of context's. OTB Context's are stored in a browser cookie with some content stored
 on the users profile in order to tie together the cortex and aem users and validate they are the same. The OTB implementation is an OSGI service.
+ `CortexClientFactory` provides access to the `CortexClient` facade.
+ `CortexSdkServiceFactory` provides access to services that manage specific aspects of the cortex API, they may in some cases span multiple resources
and in others cover just one.

Using the CortexClient
----------------------
 The cortex client (and the jax-rs client) use Model classes to encapsulate two types of information, request information and how to extract the
 relevant parts from the response.

### Request Data
Decorating the request is done using annotations, If one wanted to get the default cart for a user you would create a cart object with the following
annotations.

    @EntryPointUri({"carts", "{scope}", "default"})
    @Zoom({
	    @RelationPath({"lineitems", "element"}),
	    @RelationPath({"total"}),
    })
    public class Cart {
        .....
    }

This example will make a request to the default cart for the specified scope (this is defined when the client is created) and it will add the
specified zooms to the request. If you needed to submit some data to a form and wanted the response from that query you could use the follow location
annotation.

    @Zoom(@RelationPath("order"))
    @FollowLocation
    public class Purchase {
        .....
    }

The above example will use the `@followlocation` to follow the link on the query result (Http POST) and zoom the order on that result.

### Response Data (unmarshalling)
Extracting response data is a combination of two types of annotation.

+ `@JsonPath` is used on top level objects. Specifically on the raw response data that has not had any unmarshalling.
[JsonPath](https://github.com/jayway/JsonPath) is similar in concept to [xpath](http://www.w3schools.com/xpath/xpath_syntax.asp) but for json.
Jsonformatter provides some helpful tools:
 + [Json Validator](http://jsonformatter.curiousconcept.com)
 + [JsonPath Tester](http://jsonpath.curiousconcept.com/)

    ````
    public class Cart {
        @JsonPath("$._lineitems[0]._element")
        private List<LineItem> lineItems;
        @JsonPath("$.total-quantity")
        private int totalQuantity;
    }
    ````

The above example extracts two fragments of the response the 'total-quantity' property which is part of the top level data as well as all of the
lineitems element objects. This fragment is unmarshalled by the client and for each fragment of the array a `LineItem` is constructed using the
provided data.

+ `@JsonProperty` is used on fragments or parts of the response to extract specific properties, it only works on the current object and you cannot
call into nested properties like you can with the `@JsonPath`

    ````
    public class Address {
        @JsonProperty("street-address")
        private String streetAddress;
        @JsonProperty("country-name")
        private String countryName;
        @JsonProperty("postal-code")
        private String postalCode;
    }
    ````

### Making A Request

We first need to get a user-scoped instance of the CortexClient. You can use the `CortexClientFactory` to get an instance of the client.
To create a `CortexClient` you need access to:

1. The authentication token for the user, this needs to be obtained separately before a CortexClient can be created.
2. The scope being used on the cortex instance, in practical terms this usually corresponds to a store e.g. mobee, geometrixx

    ````
    CortexClient cortexClient = cortexClientFactory.create("dsfjklHJKASmna", "geometrixx");
    Cart cart = cortexClient.get(Cart.class);
    ````

Looking at our previous examples this request will load the cart resource '/carts/geometrixx/default' with the following zoom
'lineitems:element,totals' and extract the necessary response data into the Cart class. Submitting data as part of a request is equally simple

    Response productPrice = cortexClient.post(ImmutableMap.of("code", sku));

It is also possible to specify a class to use as part of the `post` which allows you to specify annotations on the post and automatically construct
some meaniful objects from the response

    ProductPrice productPrice = cortexClient.post(ImmutableMap.of("code", sku), ProductPrice.class);

The product price class specifies the `@followlocation` and `@Zoom` annotations. If you use this technique your class must implement `CortexResponse`
which ensures that you have access to the underlying response as in the previous example.