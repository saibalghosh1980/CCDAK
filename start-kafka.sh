systemctl start confluent-zookeeper
systemctl start confluent-kafka 
journalctl -u confluent-zookeeper -f
journalctl -u confluent-kafka -f