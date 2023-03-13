## Notes - week 5

---

**Location:** Echo Hall D\
**Date:** 15.12.2022 \
**Time:** 10:00 - 11:00 \
**Attendees:** Paul Misterka, Rafael Braga Medeiros Mota Borges, Jyotiradityaa Jaiman, Razvan Nistor, Codrin Ogreanu, Jasper van Beusekom, Hubert Nowak \
**Chair:** Razvan Nistor\
**Note taker:** Rafael Borges

---


* We have almost finished Task 1
* Paul is nodding xD when seeing the diagram
* Paul said the diagram looks good
* He says that if a microservice only has 1 subsystem, it shouldn't be a microservice on its own. This is mainly relating to the _Facutly_ microservice
* The above remark is acceptable for the _Notificaton Manager_ microservice
* The diagram should be black and white
* The code should reflect the UML diagram, however, there is no need for strictly following the diagram in low level implementation
* We have implemented more functionality and the communication between microservices is working
* We are now going to show the demo
    - We can send requests through _Postman_
    - There is still no coupling from this request to it's supposed reaction
    - There is **no return from the request** (said Paul)
    - We can accept requests
    - The login of users is currently not working

* **Remove non-testable classes from the test coverage** said Paul

* Paul's team did not test the integration with Kafka
* Don't prioritize _coulds_ and _shoulds_ haves over _musts_.
* It is okay if we don't finish all the tests said Paul
* We cannot move the functional requirements around the _Moscow_ board
* How are we going to meet **Thursday next week 10AM Dutch time**
* We are still confused about Design Patterns
* We had a _Strategy_ in mind for the Design Patterns, for the Faculty request booking. Paul approved and said it is acceptable

* **Everyone should send what they did to the group in Mattermost**
