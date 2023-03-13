# Minutes meeting week4

## Reflection on what we did
- submitted draft
- we have a working example with kafka
- started on implementing the microservices

## Requirements

### Functional
- generally nice
- A lot of branches, usually one branch per person at a time
- Specify which issue relates with which requirement
- Sometimes requirements don't make sense sometimes(because of copy paste)
- Rephrase authorization requirements
- Resource types is a must have. But having the constraints is a should have.
- Could haves are very lengthy (second one especially can be split)
- Do the very basic implementation of the microservice(do not have any logic), so we have what to work on in the future.
- Should explain the structure of the UML. Relations between classes needs to be more clear.
- For example, specify that you can add or modify but not delete
- Who assignes employees to faculty?

### Non functional
- Mention the DB we are using
- Mention the networking architecture (mention Kafka)
- Mention how communication works (gateway communicates with... and with...) 
- Mention testing covarage

## Draft
- remove first part
- description nice and concise
- explanations needed and Paul likes them:)
- DDD -> granularity *keep it the same throughout the diagram
- When looking at the diagram it should read a sentence (it also helps with finding problems with relations)
    * what is being sent to notification manager by faculty?
- clean the diagram a bit

### Components
- Microservices need to be a subsystem
- Microservices should have `<<component>>`
- And subcomponents noted as components
- wwithin the components there should be other components and stuff needs to come out (endpoints or other microservices)
- If microservice is empty then it should just be a service
- Lollipops should come out of services from microservices, not microservices directly.
- Look at example sent from Paul
- Should have ports (square thingy)
- Mention where and what the DB are (can be put in the diagram or the description of the diagram)
- Human readable names (e.g. "Adding nodes")
- Talk about the architecture inside the microservices and why
- Mention "we have exactly `n` microservices and why we have `n`".
- Elaborate on: 
    * Nature of communication between microservices
    * Security

## Design Patterns
- design patterns help when the application becomes bigger
- no pattern seem to fit our requirements
- we decided on facade, builder, singleton.
- Tip: look into constraints and strategy patterns and it might help narrow the deeper requirements.
- Apply builder only for things where it makes sense for it to be used.

## Testing in Kafka
- it is an integration test and it should take that long
- we can make it an optional step in the pipeline

## PMD
- can not disable it
- use DTOs and manually put them in the gateway
- every microservice is dependent on the gateway, all the DTO can be put there 
- gateway should **NOT** import from other microservices.
- can disable *some* warnings

## Request with name and description
- Client needs that, so we have to do it
- Assign any meaning we want to that

## Class diagram
- UML for every design pattern
- not for the whole system

## Nodes
- token is like a password
- be able to acces the node with the token (Could haves)

## Important thingy
- task division must be done together
- if you feel excluded in any way or for any other concerns, you can talk privately to Paul
- prepare a demo for next week, make sure it works.
