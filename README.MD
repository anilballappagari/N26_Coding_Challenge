==================================================
Brief intro on how and behavior of the Application
==================================================

1) Application is built on Spring Boot and can be access through 8080 port.
2) /transactions and /statistics are the REST API exposed.
3) When the /transactions is called it verifies for transaction expiry. If the transaction expired i.e., less than 60 secs of current time,
	then that transaction is reject throwing 204. If the transaction is valid behavior as follows.
		1) Sends a response as 201.
		2) The valid transaction is put to TransactionStore (In-Memory datastore)
		3) The PerformAdd worker thread picks any valid transaction and performs the calculation of statistics.
		4) This worker thread moves the valid transaction to completed transaction list in TransactionStore.
		5) The PerformRemove worker thread picks any completed transaction and looks for expired transactions. If expired it perform 			calculations to remove from statistics.
4) Each worker thread runs at an interval 1 second
5) The output will be to near-perfect.
6) I haven't used any In-Memory solutions like Hazelcast, ehcache as List and Map would suffice this requirement as we would be clearing the expired transactions every second.