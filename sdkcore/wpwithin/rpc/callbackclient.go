package rpc

import (
	"crypto/tls"
	"fmt"

	"git.apache.org/thrift.git/lib/go/thrift"
	"innovation.worldpay.com/worldpay-within-sdk/sdkcore/wpwithin/rpc/wpthrift"
"innovation.worldpay.com/worldpay-within-sdk/sdkcore/wpwithin/types"
	"innovation.worldpay.com/worldpay-within-sdk/sdkcore/wpwithin/utils"
	"innovation.worldpay.com/worldpay-within-sdk/sdkcore/wpwithin/rpc/wpthrift/wpthrift_types"
)

type CallbackClient interface {

	BeginServiceDelivery(clientID string, serviceDeliveryToken types.ServiceDeliveryToken, unitsToSupply int)
	EndServiceDelivery(clientID string, serviceDeliveryToken types.ServiceDeliveryToken, unitsReceived int)
}

type CallbackClientImpl struct {

	client wpthrift.WPWithinCallback
}

func (cb *CallbackClientImpl) BeginServiceDelivery(clientID string, serviceDeliveryToken types.ServiceDeliveryToken, unitsToSupply int) {

	sdt := wpthrift_types.ServiceDeliveryToken{

		Key: serviceDeliveryToken.Key,
		Issued: utils.TimeFormatISO(serviceDeliveryToken.Issued),
		Expiry: utils.TimeFormatISO(serviceDeliveryToken.Expiry),
		RefundOnExpiry: serviceDeliveryToken.RefundOnExpiry,
		Signature:serviceDeliveryToken.Signature,
	}

	cb.client.BeginServiceDelivery(clientID, &sdt, int32(unitsToSupply))
}

func (cb *CallbackClientImpl) EndServiceDelivery(clientID string, serviceDeliveryToken types.ServiceDeliveryToken, unitsReceived int) {

	sdt := wpthrift_types.ServiceDeliveryToken{

		Key: serviceDeliveryToken.Key,
		Issued: utils.TimeFormatISO(serviceDeliveryToken.Issued),
		Expiry: utils.TimeFormatISO(serviceDeliveryToken.Expiry),
		RefundOnExpiry: serviceDeliveryToken.RefundOnExpiry,
		Signature:serviceDeliveryToken.Signature,
	}

	cb.client.EndServiceDelivery(clientID, &sdt, int32(unitsReceived))
}

func NewCallback(cfg Configuration) (CallbackClient, error) {

	protocolFactory := thrift.NewTBinaryProtocolFactory(true, true)
	transportFactory := thrift.NewTBufferedTransportFactory(8192)

	var transport thrift.TTransport
	var err error

	addr := fmt.Sprintf("%s:%d", cfg.Host, cfg.CallbackPort)

	if cfg.Secure {
		tlsCfg := new(tls.Config)
		tlsCfg.InsecureSkipVerify = true
		transport, err = thrift.NewTSSLSocket(addr, tlsCfg)
	} else {
		transport, err = thrift.NewTSocket(addr)
	}
	if err != nil {
		fmt.Println("Error opening socket:", err)
		return nil, err
	}
	transport = transportFactory.GetTransport(transport)
	defer transport.Close()
	if err := transport.Open(); err != nil {
		return nil, err
	}

	result := &CallbackClientImpl{
		client: wpthrift.NewWPWithinCallbackClientFactory(transport, protocolFactory),
	}

	return result, nil
}
