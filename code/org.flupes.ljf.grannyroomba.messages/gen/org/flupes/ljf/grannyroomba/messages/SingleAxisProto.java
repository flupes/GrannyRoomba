// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: SingleAxis.proto

package org.flupes.ljf.grannyroomba.messages;

public final class SingleAxisProto {
  private SingleAxisProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface SingleAxisCmdOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;
    /**
     * <code>required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;</code>
     */
    boolean hasCmd();
    /**
     * <code>required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;</code>
     */
    org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command getCmd();

    // optional .grannyroomba.messages.MotorMsg msg = 2;
    /**
     * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
     */
    boolean hasMsg();
    /**
     * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
     */
    org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg getMsg();
    /**
     * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
     */
    org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsgOrBuilder getMsgOrBuilder();
  }
  /**
   * Protobuf type {@code grannyroomba.messages.SingleAxisCmd}
   */
  public static final class SingleAxisCmd extends
      com.google.protobuf.GeneratedMessage
      implements SingleAxisCmdOrBuilder {
    // Use SingleAxisCmd.newBuilder() to construct.
    private SingleAxisCmd(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private SingleAxisCmd(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final SingleAxisCmd defaultInstance;
    public static SingleAxisCmd getDefaultInstance() {
      return defaultInstance;
    }

    public SingleAxisCmd getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private SingleAxisCmd(
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
            case 8: {
              int rawValue = input.readEnum();
              org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command value = org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(1, rawValue);
              } else {
                bitField0_ |= 0x00000001;
                cmd_ = value;
              }
              break;
            }
            case 18: {
              org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.Builder subBuilder = null;
              if (((bitField0_ & 0x00000002) == 0x00000002)) {
                subBuilder = msg_.toBuilder();
              }
              msg_ = input.readMessage(org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(msg_);
                msg_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000002;
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
      return org.flupes.ljf.grannyroomba.messages.SingleAxisProto.internal_static_grannyroomba_messages_SingleAxisCmd_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.flupes.ljf.grannyroomba.messages.SingleAxisProto.internal_static_grannyroomba_messages_SingleAxisCmd_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.class, org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Builder.class);
    }

    public static com.google.protobuf.Parser<SingleAxisCmd> PARSER =
        new com.google.protobuf.AbstractParser<SingleAxisCmd>() {
      public SingleAxisCmd parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SingleAxisCmd(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<SingleAxisCmd> getParserForType() {
      return PARSER;
    }

    /**
     * Protobuf enum {@code grannyroomba.messages.SingleAxisCmd.Command}
     */
    public enum Command
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>SET_MOTOR = 1;</code>
       */
      SET_MOTOR(0, 1),
      /**
       * <code>GET_STATE = 2;</code>
       */
      GET_STATE(1, 2),
      /**
       * <code>GET_CONFIG = 3;</code>
       */
      GET_CONFIG(2, 3),
      ;

      /**
       * <code>SET_MOTOR = 1;</code>
       */
      public static final int SET_MOTOR_VALUE = 1;
      /**
       * <code>GET_STATE = 2;</code>
       */
      public static final int GET_STATE_VALUE = 2;
      /**
       * <code>GET_CONFIG = 3;</code>
       */
      public static final int GET_CONFIG_VALUE = 3;


      public final int getNumber() { return value; }

      public static Command valueOf(int value) {
        switch (value) {
          case 1: return SET_MOTOR;
          case 2: return GET_STATE;
          case 3: return GET_CONFIG;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<Command>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static com.google.protobuf.Internal.EnumLiteMap<Command>
          internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<Command>() {
              public Command findValueByNumber(int number) {
                return Command.valueOf(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(index);
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.getDescriptor().getEnumTypes().get(0);
      }

      private static final Command[] VALUES = values();

      public static Command valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int index;
      private final int value;

      private Command(int index, int value) {
        this.index = index;
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:grannyroomba.messages.SingleAxisCmd.Command)
    }

    private int bitField0_;
    // required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;
    public static final int CMD_FIELD_NUMBER = 1;
    private org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command cmd_;
    /**
     * <code>required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;</code>
     */
    public boolean hasCmd() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;</code>
     */
    public org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command getCmd() {
      return cmd_;
    }

    // optional .grannyroomba.messages.MotorMsg msg = 2;
    public static final int MSG_FIELD_NUMBER = 2;
    private org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg msg_;
    /**
     * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
     */
    public boolean hasMsg() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
     */
    public org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg getMsg() {
      return msg_;
    }
    /**
     * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
     */
    public org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsgOrBuilder getMsgOrBuilder() {
      return msg_;
    }

    private void initFields() {
      cmd_ = org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command.SET_MOTOR;
      msg_ = org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.getDefaultInstance();
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasCmd()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (hasMsg()) {
        if (!getMsg().isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeEnum(1, cmd_.getNumber());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeMessage(2, msg_);
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
          .computeEnumSize(1, cmd_.getNumber());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, msg_);
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

    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd prototype) {
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
     * Protobuf type {@code grannyroomba.messages.SingleAxisCmd}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmdOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.flupes.ljf.grannyroomba.messages.SingleAxisProto.internal_static_grannyroomba_messages_SingleAxisCmd_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.flupes.ljf.grannyroomba.messages.SingleAxisProto.internal_static_grannyroomba_messages_SingleAxisCmd_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.class, org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Builder.class);
      }

      // Construct using org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.newBuilder()
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
          getMsgFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        cmd_ = org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command.SET_MOTOR;
        bitField0_ = (bitField0_ & ~0x00000001);
        if (msgBuilder_ == null) {
          msg_ = org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.getDefaultInstance();
        } else {
          msgBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.flupes.ljf.grannyroomba.messages.SingleAxisProto.internal_static_grannyroomba_messages_SingleAxisCmd_descriptor;
      }

      public org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd getDefaultInstanceForType() {
        return org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.getDefaultInstance();
      }

      public org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd build() {
        org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd buildPartial() {
        org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd result = new org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.cmd_ = cmd_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        if (msgBuilder_ == null) {
          result.msg_ = msg_;
        } else {
          result.msg_ = msgBuilder_.build();
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd) {
          return mergeFrom((org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd other) {
        if (other == org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.getDefaultInstance()) return this;
        if (other.hasCmd()) {
          setCmd(other.getCmd());
        }
        if (other.hasMsg()) {
          mergeMsg(other.getMsg());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasCmd()) {
          
          return false;
        }
        if (hasMsg()) {
          if (!getMsg().isInitialized()) {
            
            return false;
          }
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;
      private org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command cmd_ = org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command.SET_MOTOR;
      /**
       * <code>required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;</code>
       */
      public boolean hasCmd() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;</code>
       */
      public org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command getCmd() {
        return cmd_;
      }
      /**
       * <code>required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;</code>
       */
      public Builder setCmd(org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        cmd_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required .grannyroomba.messages.SingleAxisCmd.Command cmd = 1;</code>
       */
      public Builder clearCmd() {
        bitField0_ = (bitField0_ & ~0x00000001);
        cmd_ = org.flupes.ljf.grannyroomba.messages.SingleAxisProto.SingleAxisCmd.Command.SET_MOTOR;
        onChanged();
        return this;
      }

      // optional .grannyroomba.messages.MotorMsg msg = 2;
      private org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg msg_ = org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.getDefaultInstance();
      private com.google.protobuf.SingleFieldBuilder<
          org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg, org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.Builder, org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsgOrBuilder> msgBuilder_;
      /**
       * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
       */
      public boolean hasMsg() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
       */
      public org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg getMsg() {
        if (msgBuilder_ == null) {
          return msg_;
        } else {
          return msgBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
       */
      public Builder setMsg(org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg value) {
        if (msgBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          msg_ = value;
          onChanged();
        } else {
          msgBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
       */
      public Builder setMsg(
          org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.Builder builderForValue) {
        if (msgBuilder_ == null) {
          msg_ = builderForValue.build();
          onChanged();
        } else {
          msgBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
       */
      public Builder mergeMsg(org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg value) {
        if (msgBuilder_ == null) {
          if (((bitField0_ & 0x00000002) == 0x00000002) &&
              msg_ != org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.getDefaultInstance()) {
            msg_ =
              org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.newBuilder(msg_).mergeFrom(value).buildPartial();
          } else {
            msg_ = value;
          }
          onChanged();
        } else {
          msgBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
       */
      public Builder clearMsg() {
        if (msgBuilder_ == null) {
          msg_ = org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.getDefaultInstance();
          onChanged();
        } else {
          msgBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }
      /**
       * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
       */
      public org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.Builder getMsgBuilder() {
        bitField0_ |= 0x00000002;
        onChanged();
        return getMsgFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
       */
      public org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsgOrBuilder getMsgOrBuilder() {
        if (msgBuilder_ != null) {
          return msgBuilder_.getMessageOrBuilder();
        } else {
          return msg_;
        }
      }
      /**
       * <code>optional .grannyroomba.messages.MotorMsg msg = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilder<
          org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg, org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.Builder, org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsgOrBuilder> 
          getMsgFieldBuilder() {
        if (msgBuilder_ == null) {
          msgBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg, org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsg.Builder, org.flupes.ljf.grannyroomba.messages.MotorProto.MotorMsgOrBuilder>(
                  msg_,
                  getParentForChildren(),
                  isClean());
          msg_ = null;
        }
        return msgBuilder_;
      }

      // @@protoc_insertion_point(builder_scope:grannyroomba.messages.SingleAxisCmd)
    }

    static {
      defaultInstance = new SingleAxisCmd(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:grannyroomba.messages.SingleAxisCmd)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_grannyroomba_messages_SingleAxisCmd_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_grannyroomba_messages_SingleAxisCmd_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\020SingleAxis.proto\022\025grannyroomba.message" +
      "s\032\013Motor.proto\"\261\001\n\rSingleAxisCmd\0229\n\003cmd\030" +
      "\001 \002(\0162,.grannyroomba.messages.SingleAxis" +
      "Cmd.Command\022,\n\003msg\030\002 \001(\0132\037.grannyroomba." +
      "messages.MotorMsg\"7\n\007Command\022\r\n\tSET_MOTO" +
      "R\020\001\022\r\n\tGET_STATE\020\002\022\016\n\nGET_CONFIG\020\003B7\n$or" +
      "g.flupes.ljf.grannyroomba.messagesB\017Sing" +
      "leAxisProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_grannyroomba_messages_SingleAxisCmd_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_grannyroomba_messages_SingleAxisCmd_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_grannyroomba_messages_SingleAxisCmd_descriptor,
              new java.lang.String[] { "Cmd", "Msg", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          org.flupes.ljf.grannyroomba.messages.MotorProto.getDescriptor(),
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}