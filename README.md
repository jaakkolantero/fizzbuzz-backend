# FIZZ BUZZ BACKEND

Fizzbuzz-game REST API example.

## Getting Started

Live demo running at: [fizzbuzz.tero.jaakko.la](https://fizzbuzz.tero.jaakko.la)
To run the app you need to setup a database connection.

### Create game:
```start``` and ```end``` are optional. 
```
curl -i -X POST \
   -H "Content-Type:application/json" \
   -d \
'{
  "name": "LOL",
  "start": "1",
  "end": "100"
}' \
 'https://fizzbuzz.tero.jaakko.la/v1/fizz/games'
```
### List games:
```
curl -i -X GET \
 'https://https:/fizzbuzz.tero.jaakko.la/v1/fizz/games'
```
### Find a specifig game:
```
curl -i -X GET \
 'http://fizzbuzz.tero.jaakko.la/fizz/games/3'
```
### Guessing number:
(case insensitive, returns updated state)
```
curl -i -X PATCH \
   -H "Content-Type:application/json" \
   -d \
'{
  "guess":"fizz"
}' \
 'https://fizzbuzz.tero.jaakko.la/v1/fizz/games/1'
```

Run app

```
sbt run
```
and head to http://localhost:9000/

### Prerequisites

* scala ~2.12.6
* Play framework ~2.6.18
* SBT ~1.2.4