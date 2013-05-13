// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: DriveVelocity.proto

package org.flupes.ljf.grannyroomba.messages;

public final class DriveVelocityMsg {
  private DriveVelocityMsg() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface DriveVelocityOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // optional float speed = 1 [default = 0];
    /**
     * <code>optional float speed = 1 [default = 0];</code>
     */
    boolean hasSpeed();
    /**
     * <code>optional float speed = 1 [default = 0];</code>
     */
    float getSpeed();

    // optional float radius = 2 [default = 1000];
    /**
     * <code>optional float radius = 2 [default = 1000];</code>
     */
    boolean hasRadius();
    /**
     * <code>optional float radius = 2 [default = 1000];</code>
     */
    float getRadius();

    // optional float timeout = 3 [default = 0];
    /**
     * <code>optional float timeout = 3 [default = 0];</code>
     */
    boolean hasTimeout();
    /**
     * <code>optional float timeout = 3 [default = 0];</code>
     */
    float getTimeout();
  }
  /**
   * Protobuf type {@code org.flupes.ljf.grannyroomba.messages.DriveVelocity}
   */
  public static final class DriveVelocity extends
      com.google.protobuf.GeneratedMessage
      implements DriveVelocityOrBuilder {
    // Use DriveVelocity.newBuilder() to construct.
    private DriveVelocity(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private DriveVelocity(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final DriveVelocity defaultInstance;
    public static DriveVelocity getDefaultInstance() {
      return defaultInstance;
    }

    public DriveVelocity getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private DriveVelocity(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 13: {
              bitField0_ |= 0x00000001;
              speed_ = input.readFloat();
              break;
            }
            case 21: {
              bitField0_ |= 0x00000002;
              radius_ = input.readFloat();
              break;
            }
            case 29: {
              bitField0_ |= 0x00000004;
              timeout_ = input.readFloat();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity.class, org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity.Builder.class);
    }

    public static com.google.protobuf.Parser<DriveVelocity> PARSER =
        new com.google.protobuf.AbstractParser<DriveVelocity>() {
      public DriveVelocity parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new DriveVelocity(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<DriveVelocity> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // optional float speed = 1 [default = 0];
    public static final int SPEED_FIELD_NUMBER = 1;
    private float speed_;
    /**
     * <code>optional float speed = 1 [default = 0];</code>
     */
    public boolean hasSpeed() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional float speed = 1 [default = 0];</code>
     */
    public float getSpeed() {
      return speed_;
    }

    // optional float radius = 2 [default = 1000];
    public static final int RADIUS_FIELD_NUMBER = 2;
    private float radius_;
    /**
     * <code>optional float radius = 2 [default = 1000];</code>
     */
    public boolean hasRadius() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional float radius = 2 [default = 1000];</code>
     */
    public float getRadius() {
      return radius_;
    }

    // optional float timeout = 3 [default = 0];
    public static final int TIMEOUT_FIELD_NUMBER = 3;
    private float timeout_;
    /**
     * <code>optional float timeout = 3 [default = 0];</code>
     */
    public boolean hasTimeout() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional float timeout = 3 [default = 0];</code>
     */
    public float getTimeout() {
      return timeout_;
    }

    private void initFields() {
      speed_ = 0F;
      radius_ = 1000F;
      timeout_ = 0F;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeFloat(1, speed_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeFloat(2, radius_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeFloat(3, timeout_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(1, speed_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(2, radius_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(3, timeout_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.flupes.ljf.grannyroomba.messages.DriveVelocity}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocityOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity.class, org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity.Builder.class);
      }

      // Construct using org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        speed_ = 0F;
        bitField0_ = (bitField0_ & ~0x00000001);
        radius_ = 1000F;
        bitField0_ = (bitField0_ & ~0x00000002);
        timeout_ = 0F;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_descriptor;
      }

      public org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity getDefaultInstanceForType() {
        return org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity.getDefaultInstance();
      }

      public org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity build() {
        org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity buildPartial() {
        org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity result = new org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.speed_ = speed_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.radius_ = radius_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.timeout_ = timeout_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity) {
          return mergeFrom((org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity other) {
        if (other == org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity.getDefaultInstance()) return this;
        if (other.hasSpeed()) {
          setSpeed(other.getSpeed());
        }
        if (other.hasRadius()) {
          setRadius(other.getRadius());
        }
        if (other.hasTimeout()) {
          setTimeout(other.getTimeout());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.flupes.ljf.grannyroomba.messages.DriveVelocityMsg.DriveVelocity) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // optional float speed = 1 [default = 0];
      private float speed_ ;
      /**
       * <code>optional float speed = 1 [default = 0];</code>
       */
      public boolean hasSpeed() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional float speed = 1 [default = 0];</code>
       */
      public float getSpeed() {
        return speed_;
      }
      /**
       * <code>optional float speed = 1 [default = 0];</code>
       */
      public Builder setSpeed(float value) {
        bitField0_ |= 0x00000001;
        speed_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional float speed = 1 [default = 0];</code>
       */
      public Builder clearSpeed() {
        bitField0_ = (bitField0_ & ~0x00000001);
        speed_ = 0F;
        onChanged();
        return this;
      }

      // optional float radius = 2 [default = 1000];
      private float radius_ = 1000F;
      /**
       * <code>optional float radius = 2 [default = 1000];</code>
       */
      public boolean hasRadius() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional float radius = 2 [default = 1000];</code>
       */
      public float getRadius() {
        return radius_;
      }
      /**
       * <code>optional float radius = 2 [default = 1000];</code>
       */
      public Builder setRadius(float value) {
        bitField0_ |= 0x00000002;
        radius_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional float radius = 2 [default = 1000];</code>
       */
      public Builder clearRadius() {
        bitField0_ = (bitField0_ & ~0x00000002);
        radius_ = 1000F;
        onChanged();
        return this;
      }

      // optional float timeout = 3 [default = 0];
      private float timeout_ ;
      /**
       * <code>optional float timeout = 3 [default = 0];</code>
       */
      public boolean hasTimeout() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional float timeout = 3 [default = 0];</code>
       */
      public float getTimeout() {
        return timeout_;
      }
      /**
       * <code>optional float timeout = 3 [default = 0];</code>
       */
      public Builder setTimeout(float value) {
        bitField0_ |= 0x00000004;
        timeout_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional float timeout = 3 [default = 0];</code>
       */
      public Builder clearTimeout() {
        bitField0_ = (bitField0_ & ~0x00000004);
        timeout_ = 0F;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:org.flupes.ljf.grannyroomba.messages.DriveVelocity)
    }

    static {
      defaultInstance = new DriveVelocity(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:org.flupes.ljf.grannyroomba.messages.DriveVelocity)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023DriveVelocity.proto\022$org.flupes.ljf.gr" +
      "annyroomba.messages\"K\n\rDriveVelocity\022\020\n\005" +
      "speed\030\001 \001(\002:\0010\022\024\n\006radius\030\002 \001(\002:\0041000\022\022\n\007" +
      "timeout\030\003 \001(\002:\0010B8\n$org.flupes.ljf.grann" +
      "yroomba.messagesB\020DriveVelocityMsg"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_org_flupes_ljf_grannyroomba_messages_DriveVelocity_descriptor,
              new java.lang.String[] { "Speed", "Radius", "Timeout", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}