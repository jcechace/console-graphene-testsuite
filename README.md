Testsuite for testing webconsole UI of EAP6 server

testsuite uses arquillian grapghene for Web UI testing


#########################################################
Important notes
Graphene doesn't run with newest version of firefox, I am testing it with Firefox 17 and it works.
You can use -Darq.extension.webdriver.firefox_binary=/path/to/firefox_binary to define what firefox binary should be 
used

Steps needed to run tests:
* first unsecure the management interface in EAP (authentication is not yet properly handled in the tests)
* then run: mvn test -Djboss.dist=$JBOSS_HOME
