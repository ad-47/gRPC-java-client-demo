package com.anand.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.anand.grpc.UserServiceGrpc.UserServiceBlockingStub;
import com.anand.grpc.UserServiceGrpc.UserServiceStub;
import com.anand.grpc.UserServiceOuterClass.AddUserRequest;
import com.anand.grpc.UserServiceOuterClass.GetAllUserRequest;
import com.anand.grpc.UserServiceOuterClass.GetAllUserRequest.Response;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/** Call grpc server using port 8080 */
public class App {

  private static Logger logger = Logger.getLogger(App.class.getName());

  public static void main(String[] args) {

    // Channel is the abstraction to connect to a service endpoint
    // Let's use plaintext communication because we don't have certs
    final ManagedChannel channel =
        ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext().build();

    switch (Integer.parseInt(args[0])) {
      case 0:
        addUser(channel);
        // A Channel should be shutdown before stopping the process.
        channel.shutdown();
        break;
      case 1:
        getAllUserWithStream(channel);
        break;
      default:
        getAllUser(channel);
        // A Channel should be shutdown before stopping the process.
        channel.shutdown();
        break;
    }
  }

  private static void addUser(ManagedChannel channel) {
    // It is up to the client to determine whether to block the call
    // Here we create a blocking stub, but an async stub,
    // or an async stub with Future are always possible.
    UserServiceBlockingStub blockingStub = UserServiceGrpc.newBlockingStub(channel);

    List<String> hobbies = new ArrayList<>();
    hobbies.add("Coding " + Math.random());
    hobbies.add("Playing PubG" + Math.random());
    hobbies.add("Cycling" + Math.random());
    String userName = "Anand Jakhaniya " + UUID.randomUUID().toString();
    AddUserRequest request =
        AddUserRequest.newBuilder().setName(userName).addAllHobbies(hobbies).build();

    // Finally, make the call using the stub
    AddUserRequest.Response response = blockingStub.addUser(request);
    logger.log(Level.INFO, "User added successfully ");
    logger.log(Level.INFO, "User Info : \n" + response.getUser());
  }

  private static void getAllUser(ManagedChannel channel) {
    // It is up to the client to determine whether to block the call
    // Here we create a blocking stub, but an async stub,
    // or an async stub with Future are always possible.
    UserServiceBlockingStub blockingStub = UserServiceGrpc.newBlockingStub(channel);

    GetAllUserRequest request = GetAllUserRequest.newBuilder().build();

    // Finally, make the call using the stub
    GetAllUserRequest.Response response = blockingStub.getAllUser(request);
    logger.log(Level.INFO, "User getting successfully ");
    logger.log(Level.INFO, "User Info List : \n" + response.getUsersList());
  }

  private static void getAllUserWithStream(ManagedChannel channel) {
    // It is up to the client to determine whether to block the call
    // Here we create a blocking stub, but an async stub,
    // or an async stub with Future are always possible.
    UserServiceStub stub = UserServiceGrpc.newStub(channel);

    logger.log(Level.INFO, "Client getAllUserWithStream ");

    GetAllUserRequest request = GetAllUserRequest.newBuilder().build();
    stub.getAllUserWithStream(
        request,
        new StreamObserver<GetAllUserRequest.Response>() {

          @Override
          public void onNext(Response value) {
            logger.log(Level.INFO, "User getting successfully ");
            logger.log(Level.INFO, "User Info : \n" + value.getUser());
            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              logger.log(Level.WARNING, "Thread interrupted", e);
            }
          }

          @Override
          public void onError(Throwable t) {
            logger.log(Level.WARNING, t.getMessage());
          }

          @Override
          public void onCompleted() {
            logger.log(Level.INFO, "Getting all users successfully");
            logger.log(Level.INFO, "Task completed");

            // Typically you'll shutdown the channel somewhere else.
            // But for the purpose of the lab, we are only making a single
            // request. We'll shutdown as soon as this request is done.
            channel.shutdownNow();
          }
        });
  }
}
