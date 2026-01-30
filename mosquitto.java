import org.eclipse.paho.client.mqttv3.*;

public class MosquittoClient {

    private static final String BROKER_URL = "tcp://localhost:1883";
    private static final String CLIENT_ID = "java-mqtt-client-001";
    private static final String TOPIC = "teste/mosquitto";

    public static void main(String[] args) {

        try {
            MqttClient client = new MqttClient(
                    BROKER_URL,
                    CLIENT_ID,
                    new MemoryPersistence()
            );

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);

            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("❌ Conexão perdida: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("📩 Mensagem recebida:");
                    System.out.println("Tópico: " + topic);
                    System.out.println("Payload: " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("✅ Mensagem entregue");
                }
            });

            System.out.println("🔌 Conectando ao Mosquitto...");
            client.connect(options);
            System.out.println("✅ Conectado!");

            client.subscribe(TOPIC);
            System.out.println("📡 Inscrito no tópico: " + TOPIC);

            String payload = "Hello Mosquitto from Java!";
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);

            client.publish(TOPIC, message);
            System.out.println("🚀 Mensagem publicada");

            // Mantém a aplicação viva para receber mensagens
            Thread.sleep(30000);

            client.disconnect();
            System.out.println("🔌 Desconectado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
