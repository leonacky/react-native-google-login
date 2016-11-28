//
//  RNGoogleLogin.h
//  RNGoogleLogin
//
//  Created by Tuan Dinh on 11/28/16.
//  Copyright © 2016 Aotasoft. All rights reserved.
//

#import "RCTBridgeModule.h"

#import <Foundation/Foundation.h>
#import <GoogleSignIn/GoogleSignIn.h>

@interface RNGoogleLogin : NSObject <RCTBridgeModule, UIApplicationDelegate, GIDSignInDelegate>

+ (BOOL)application:(UIApplication *)application openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication annotation:(id)annotation;

@end
