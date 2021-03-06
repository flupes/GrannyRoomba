// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: RoombaService.proto

package org.flupes.ljf.grannyroomba.messages;

public final class RoombaService {
  private RoombaService() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  /**
   * Protobuf service {@code grannyroomba.messages.LocomotorService}
   */
  public static abstract class LocomotorService
      implements com.google.protobuf.Service {
    protected LocomotorService() {}

    public interface Interface {
      /**
       * <code>rpc stop(.grannyroomba.messages.StopMsg) returns (.grannyroomba.messages.CommandStatus);</code>
       */
      public abstract void stop(
          com.google.protobuf.RpcController controller,
          org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg request,
          com.google.protobuf.RpcCallback<org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus> done);

      /**
       * <code>rpc driveVelocity(.grannyroomba.messages.DriveVelocityMsg) returns (.grannyroomba.messages.RoombaStatus);</code>
       */
      public abstract void driveVelocity(
          com.google.protobuf.RpcController controller,
          org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg request,
          com.google.protobuf.RpcCallback<org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus> done);

    }

    public static com.google.protobuf.Service newReflectiveService(
        final Interface impl) {
      return new LocomotorService() {
        @java.lang.Override
        public  void stop(
            com.google.protobuf.RpcController controller,
            org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg request,
            com.google.protobuf.RpcCallback<org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus> done) {
          impl.stop(controller, request, done);
        }

        @java.lang.Override
        public  void driveVelocity(
            com.google.protobuf.RpcController controller,
            org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg request,
            com.google.protobuf.RpcCallback<org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus> done) {
          impl.driveVelocity(controller, request, done);
        }

      };
    }

    public static com.google.protobuf.BlockingService
        newReflectiveBlockingService(final BlockingInterface impl) {
      return new com.google.protobuf.BlockingService() {
        public final com.google.protobuf.Descriptors.ServiceDescriptor
            getDescriptorForType() {
          return getDescriptor();
        }

        public final com.google.protobuf.Message callBlockingMethod(
            com.google.protobuf.Descriptors.MethodDescriptor method,
            com.google.protobuf.RpcController controller,
            com.google.protobuf.Message request)
            throws com.google.protobuf.ServiceException {
          if (method.getService() != getDescriptor()) {
            throw new java.lang.IllegalArgumentException(
              "Service.callBlockingMethod() given method descriptor for " +
              "wrong service type.");
          }
          switch(method.getIndex()) {
            case 0:
              return impl.stop(controller, (org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg)request);
            case 1:
              return impl.driveVelocity(controller, (org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg)request);
            default:
              throw new java.lang.AssertionError("Can't get here.");
          }
        }

        public final com.google.protobuf.Message
            getRequestPrototype(
            com.google.protobuf.Descriptors.MethodDescriptor method) {
          if (method.getService() != getDescriptor()) {
            throw new java.lang.IllegalArgumentException(
              "Service.getRequestPrototype() given method " +
              "descriptor for wrong service type.");
          }
          switch(method.getIndex()) {
            case 0:
              return org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg.getDefaultInstance();
            case 1:
              return org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg.getDefaultInstance();
            default:
              throw new java.lang.AssertionError("Can't get here.");
          }
        }

        public final com.google.protobuf.Message
            getResponsePrototype(
            com.google.protobuf.Descriptors.MethodDescriptor method) {
          if (method.getService() != getDescriptor()) {
            throw new java.lang.IllegalArgumentException(
              "Service.getResponsePrototype() given method " +
              "descriptor for wrong service type.");
          }
          switch(method.getIndex()) {
            case 0:
              return org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.getDefaultInstance();
            case 1:
              return org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus.getDefaultInstance();
            default:
              throw new java.lang.AssertionError("Can't get here.");
          }
        }

      };
    }

    /**
     * <code>rpc stop(.grannyroomba.messages.StopMsg) returns (.grannyroomba.messages.CommandStatus);</code>
     */
    public abstract void stop(
        com.google.protobuf.RpcController controller,
        org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg request,
        com.google.protobuf.RpcCallback<org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus> done);

    /**
     * <code>rpc driveVelocity(.grannyroomba.messages.DriveVelocityMsg) returns (.grannyroomba.messages.RoombaStatus);</code>
     */
    public abstract void driveVelocity(
        com.google.protobuf.RpcController controller,
        org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg request,
        com.google.protobuf.RpcCallback<org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus> done);

    public static final
        com.google.protobuf.Descriptors.ServiceDescriptor
        getDescriptor() {
      return org.flupes.ljf.grannyroomba.messages.RoombaService.getDescriptor().getServices().get(0);
    }
    public final com.google.protobuf.Descriptors.ServiceDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }

    public final void callMethod(
        com.google.protobuf.Descriptors.MethodDescriptor method,
        com.google.protobuf.RpcController controller,
        com.google.protobuf.Message request,
        com.google.protobuf.RpcCallback<
          com.google.protobuf.Message> done) {
      if (method.getService() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "Service.callMethod() given method descriptor for wrong " +
          "service type.");
      }
      switch(method.getIndex()) {
        case 0:
          this.stop(controller, (org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg)request,
            com.google.protobuf.RpcUtil.<org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus>specializeCallback(
              done));
          return;
        case 1:
          this.driveVelocity(controller, (org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg)request,
            com.google.protobuf.RpcUtil.<org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus>specializeCallback(
              done));
          return;
        default:
          throw new java.lang.AssertionError("Can't get here.");
      }
    }

    public final com.google.protobuf.Message
        getRequestPrototype(
        com.google.protobuf.Descriptors.MethodDescriptor method) {
      if (method.getService() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "Service.getRequestPrototype() given method " +
          "descriptor for wrong service type.");
      }
      switch(method.getIndex()) {
        case 0:
          return org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg.getDefaultInstance();
        case 1:
          return org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg.getDefaultInstance();
        default:
          throw new java.lang.AssertionError("Can't get here.");
      }
    }

    public final com.google.protobuf.Message
        getResponsePrototype(
        com.google.protobuf.Descriptors.MethodDescriptor method) {
      if (method.getService() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "Service.getResponsePrototype() given method " +
          "descriptor for wrong service type.");
      }
      switch(method.getIndex()) {
        case 0:
          return org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.getDefaultInstance();
        case 1:
          return org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus.getDefaultInstance();
        default:
          throw new java.lang.AssertionError("Can't get here.");
      }
    }

    public static Stub newStub(
        com.google.protobuf.RpcChannel channel) {
      return new Stub(channel);
    }

    public static final class Stub extends org.flupes.ljf.grannyroomba.messages.RoombaService.LocomotorService implements Interface {
      private Stub(com.google.protobuf.RpcChannel channel) {
        this.channel = channel;
      }

      private final com.google.protobuf.RpcChannel channel;

      public com.google.protobuf.RpcChannel getChannel() {
        return channel;
      }

      public  void stop(
          com.google.protobuf.RpcController controller,
          org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg request,
          com.google.protobuf.RpcCallback<org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus> done) {
        channel.callMethod(
          getDescriptor().getMethods().get(0),
          controller,
          request,
          org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.getDefaultInstance(),
          com.google.protobuf.RpcUtil.generalizeCallback(
            done,
            org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.class,
            org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.getDefaultInstance()));
      }

      public  void driveVelocity(
          com.google.protobuf.RpcController controller,
          org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg request,
          com.google.protobuf.RpcCallback<org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus> done) {
        channel.callMethod(
          getDescriptor().getMethods().get(1),
          controller,
          request,
          org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus.getDefaultInstance(),
          com.google.protobuf.RpcUtil.generalizeCallback(
            done,
            org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus.class,
            org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus.getDefaultInstance()));
      }
    }

    public static BlockingInterface newBlockingStub(
        com.google.protobuf.BlockingRpcChannel channel) {
      return new BlockingStub(channel);
    }

    public interface BlockingInterface {
      public org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus stop(
          com.google.protobuf.RpcController controller,
          org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg request)
          throws com.google.protobuf.ServiceException;

      public org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus driveVelocity(
          com.google.protobuf.RpcController controller,
          org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg request)
          throws com.google.protobuf.ServiceException;
    }

    private static final class BlockingStub implements BlockingInterface {
      private BlockingStub(com.google.protobuf.BlockingRpcChannel channel) {
        this.channel = channel;
      }

      private final com.google.protobuf.BlockingRpcChannel channel;

      public org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus stop(
          com.google.protobuf.RpcController controller,
          org.flupes.ljf.grannyroomba.messages.StopProto.StopMsg request)
          throws com.google.protobuf.ServiceException {
        return (org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus) channel.callBlockingMethod(
          getDescriptor().getMethods().get(0),
          controller,
          request,
          org.flupes.ljf.grannyroomba.messages.CommandStatusProto.CommandStatus.getDefaultInstance());
      }


      public org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus driveVelocity(
          com.google.protobuf.RpcController controller,
          org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.DriveVelocityMsg request)
          throws com.google.protobuf.ServiceException {
        return (org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus) channel.callBlockingMethod(
          getDescriptor().getMethods().get(1),
          controller,
          request,
          org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.RoombaStatus.getDefaultInstance());
      }

    }

    // @@protoc_insertion_point(class_scope:grannyroomba.messages.LocomotorService)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023RoombaService.proto\022\025grannyroomba.mess" +
      "ages\032\nStop.proto\032\023DriveVelocity.proto\032\023C" +
      "ommandStatus.proto\032\022RoombaStatus.proto2\277" +
      "\001\n\020LocomotorService\022L\n\004stop\022\036.grannyroom" +
      "ba.messages.StopMsg\032$.grannyroomba.messa" +
      "ges.CommandStatus\022]\n\rdriveVelocity\022\'.gra" +
      "nnyroomba.messages.DriveVelocityMsg\032#.gr" +
      "annyroomba.messages.RoombaStatusB)\n$org." +
      "flupes.ljf.grannyroomba.messages\210\001\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          org.flupes.ljf.grannyroomba.messages.StopProto.getDescriptor(),
          org.flupes.ljf.grannyroomba.messages.DriveVelocityProto.getDescriptor(),
          org.flupes.ljf.grannyroomba.messages.CommandStatusProto.getDescriptor(),
          org.flupes.ljf.grannyroomba.messages.RoombaStatusProto.getDescriptor(),
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
