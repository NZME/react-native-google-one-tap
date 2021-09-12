import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { GoogleOneTap } from 'react-native-google-one-tap';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();

  React.useEffect(() => {
    setResult(1);
  }, []);

  const onPressLogin = async (): Promise<void> => {
    try {
      GoogleOneTap.configure({
        // webClientId:
        //   '279127187233-nu78ncani0evub19c603i799iqd7ac3j.apps.googleusercontent.com',
        webClientId:
          '511397237249-38sbi7ovd5ahct4krknfu7sg7n1tl0jv.apps.googleusercontent.com',
      });
      const userInfo = await GoogleOneTap.signIn();
      console.log('userInfo', userInfo);
    } catch (e) {
      console.log('e', e);
      setResult(3);
    }

    setResult(2);
  };

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <TouchableOpacity onPress={onPressLogin}>
        <Text style={styles.inlineLink}>Sign In</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  inlineLink: {
    fontSize: 19,
    lineHeight: 24,
    color: '#35A1D7',
  },
});
