# fight-to-flight
This is a simple program that watches and logs the prices for flight tickets from a given origin and destination. It only checks SAS at the moment.

## Structure
The program is created using actors. There is a main method that gets the wheels in motion.
Flow of the program:
![the structure](https://github.com/nielspedersen/fight-to-flight/edit/master/fight-to-flight-model.png)

### Main method
The main method estrablishes a scheduler that sends a message to `theSetActor` to check if any `records` need a refresh. A `record` is a case class: 
```scala
case class Record(
  searchTime: DateTime,
  totalPrice: Float,
  outPrice: Float,
  inPrice: Float,
  outTime: DateTime,
  inTime: DateTime
)
```
`outTime` and `inTime` are the dates representing departing from the place of origin, and returning again. The `DateTime` objects are `org.joda.time.DateTime` objects.

### timeSetActor
This actor is responsible for keeping track of when a price was found for two dates. It has a `scala.collections.mutable.Set[Record]()` to do this. The main method sends a message when this actor should check again.

When checking, a message is sent to an actor router.
When given a new record, the set is updated, and the same record is forwarded to the outputting actor (TODO: implement)
### requestRouterActor
This actor serves as a request distribution hub. It will 'load balance' a given number of `RequestActor`s and forward flight price requests to these.

### requestActor
These are responsible for making a request with the SAS REST API and retrieve a ticket price. They package the result in a `Record` and return it to the `timeSetActor` for it to process.

### outputActor
TODO: implement
