# jorre
Java Object Request Response Exchange

Jorre is a java networking framework based on netty (http://netty.io) that can be used to exchange java objects between remote client and server applications.

##Introduction

Jorre builds on netty to provide a simplifed client server model with a typical request/response message exchange.  This is done by extending the  __Client__ and __Server__ classes.  A messaging protocol is defined by extending the __ClientInterface__ and __ServerInterface__ interfaces.  Custom requests and responses are made possible by extending the base jorre message classes __ServerMessage__, __ServerRequest__, __ClientResponse__ and __ClientCallback__.

Jorre allows serveral types of messages to be exchanged between clients and servers.  They are: 

  * __ServerMessage__ this object is sent from a client to a server without expecting any response
  * __ServerRequest__ this object is sent from a client to a server and implies that a __ClientResponse__ object will be returned from the server at some future time
  * __ClientResponse__ this object is sent from a server to a client in response to a __ServerRequest__ receivied from the client
  * __ClientCallback__ objects are sent from a server to a client

Each of the above objects have a method named __handle__ for handling those messages once they are receivied.  This is where the intial logic for handling messages, requests, responses and callbacks is implemented.

The __Client__ class provides methods for sending messages and requests to a server.

  * __sendMessage__ - send a __ServerMessage__ object to the server
  * __sendRequest__ - send a __ServerRequest__ object to the server and return a __ResponseFuture__ that will be notified when the __ClientResponse__ is ready
  * __sendBlockingRequest__ - send a __Request__ object to the server and block until the __ClientResponse__ is ready

The __Server__ class provides a method for sending a callback to a client

  * __callback__ - send a __ClientCallback__ object to a client


##Example Projects

An example chat application has been included to illustrate how to use jorre.  A complete client-server implementation will require 3 projects.

  * A protocol project that defines the client-server interface - the example project is named example-chat-protocol
  * A client project that provides the implementation of the client - the example project is named example-chat-client
  * A server project that provides the implementation of the server - the example project is named example-chat-server

###Protocol project

  This project will provide 2 interfaces, one that extends the __ClientInterface__ and one that extends the __ServerInterface__. It will also provide any objects that will be passed between the client and the server.  This includes any __ServerMessage__, __ServerRequest__, __ClientResponse__ and __ClientCallback__ objects that will be extended.

###Client project

  This project will provide a client class that extends __Client__ and implements the client interface that is defined in the Protocol project. 
  
###Server project

  This project will provide a server class that extends __Server__ and implements the server interface that is defined in the Protocol project.
  