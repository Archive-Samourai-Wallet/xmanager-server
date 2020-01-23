package com.samourai.xmanager.server.utils;

import com.samourai.javaserver.utils.ServerUtils;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import com.samourai.wallet.util.FormatsUtilGeneric;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;

public class Utils {
  private static Utils instance;
  private static final ServerUtils serverUtils = ServerUtils.getInstance();

  public static final int CHAIN_RECEIVE = 0;

  public static Utils getInstance() {
    if (instance == null) {
      instance = new Utils();
    }
    return instance;
  }

  public void setLoggerDebug() {
    serverUtils.setLoggerDebug("com.samourai.xmanager");
    serverUtils.setLoggerDebug("com.samourai.wallet");
  }

  public String computeXpubAddressBech32(
      int x, String xpub, int chainIndex, NetworkParameters params) {
    DeterministicKey mKey = FormatsUtilGeneric.getInstance().createMasterPubKeyFromXPub(xpub);
    DeterministicKey cKey =
        HDKeyDerivation.deriveChildKey(mKey, new ChildNumber(chainIndex, false));
    DeterministicKey adk = HDKeyDerivation.deriveChildKey(cKey, new ChildNumber(x, false));
    ECKey feeECKey = ECKey.fromPublicOnly(adk.getPubKey());
    String feeAddressBech32 =
        Bech32UtilGeneric.getInstance().toBech32(feeECKey.getPubKey(), params);
    return feeAddressBech32;
  }
}
