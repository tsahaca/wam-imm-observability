docker rm wam-imm-observability-atp-ui-service-1 
docker rm wam-imm-observability-atp-posting-service-1 
docker rm wam-imm-observability-compliance-preclear-service-1 
docker rm wam-imm-observability-postgres-atp-1 
docker rm wam-imm-observability-postgres-compliance-1 
docker rm wam-imm-observability-kafka-1 
docker rm wam-imm-observability-jaeger-all-in-one-1
docker rmi atp-ui-service:0.0.1-SNAPSHOT 
docker rmi atp-posting-service:0.0.1-SNAPSHOT 
docker rmi compliance-preclear-service:0.0.1-SNAPSHOT 

