sudo systemctl start confluent-zookeeper
sudo systemctl start confluent-kafka 
sudo journalctl -u confluent-zookeeper -f
sudo journalctl -u confluent-kafka -f