package nedioIT.kafka;
import io.gatling.javaapi.core.*;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.header.*;
import org.apache.kafka.common.header.internals.*;
import ru.tinkoff.gatling.kafka.javaapi.protocol.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.kafka.javaapi.KafkaDsl.*;


public class KafkaGatling extends Simulation {
    public static final String IP_SERVER = System.getProperty("IP_SERVER", "my-cluster-kafka-bootstrap-kafka.apps-crc.testing:443");

    private final KafkaProtocolBuilder kafkaProtocol = kafka()
            .topic("display-line-balance-producer-topic")
            .properties(
                    Map.of(
                            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, IP_SERVER,
                            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer",
                            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer",
                            "security.protocol", "SSL",
                            SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/home/othmane/Téléchargements/CrcOpenshift/truststore.jks",
                            SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password" // Mot de passe du truststore
                    )
            );



    // Le message JSON à envoyer
    private static final String jsonMessage = """
        {
            "IBSubscriber": {"ib_mdn": "123458"},
            "IBNetworkBalance": [{"ib_id": "2000"}],
            "IBOperation": {
                "origin": "CVM",
                "user": "CVM-USER",
                "uuid": "1687797837362137800"
            }
        }
    """;

    // Scénario de producteur Kafka avec une boucle de 5 envois
    private final ScenarioBuilder kafkaProducer = scenario("Kafka Producer")
            .repeat(2).on(
                    exec(kafka("Message de test kafka Indatacore")
                            .send(jsonMessage)
                    )
            );

    {
        setUp(
                kafkaProducer.injectOpen(atOnceUsers(1))
        ).protocols(kafkaProtocol);
    }
}
