//
//  RNGoogleLogin.m
//  RNGoogleLogin
//
//  Created by Tuan Dinh on 11/28/16.
//  Copyright © 2016 Aotasoft. All rights reserved.
//

#import "RNGoogleLogin.h"

@interface RNGoogleLogin() {
    RCTResponseSenderBlock mCallback;
}

@end

@implementation RNGoogleLogin

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();

- (instancetype)init
{
    self = [super init];
    if (self) {
        NSString *filePath = [[NSBundle mainBundle] pathForResource:@"Info" ofType:@"plist"];
        NSDictionary *dict = [NSDictionary dictionaryWithContentsOfFile:filePath];
        [GIDSignIn sharedInstance].delegate = self;
//        [GIDSignIn sharedInstance].scopes = dict[@"GoogleIOSScopes"];
        [GIDSignIn sharedInstance].clientID = dict[@"GoogleIOSClientId"];
    }
    return self;
}

+ (BOOL)application:(UIApplication *)application openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
    
    return [[GIDSignIn sharedInstance] handleURL:url
                               sourceApplication:sourceApplication
                                      annotation:annotation];
}

#pragma mark: - UI

- (void)signIn:(GIDSignIn *)signIn
didSignInForUser:(GIDGoogleUser *)user
     withError:(NSError *)error {
    // Perform any operations on signed in user here.
    if(error) {
        NSDictionary *loginData = @{
                                    @"eventName": @"onError",
                                    @"code": @(error.code),
                                    @"data": error.userInfo,
                                    };
        mCallback(@[@"error", loginData]);
        
    } else {
        NSString *userId = user.userID;                  // For client-side use only!
        NSString *idToken = user.authentication.idToken; // Safe to send to the server
        NSString *fullName = user.profile.name;
        NSString *email = user.profile.email;
        NSString *photo = @"";
        // ...
        NSDictionary *profile = @{
                                  @"id": userId,
                                  @"name": fullName,
                                  @"email": email,
                                  @"photo": photo
                                  };
        
        NSDictionary *credentials = @{
                                      @"userId": userId,
                                      @"token": fullName,
                                      @"serverAuthCode": email
                                      };
        
        NSDictionary *loginData = @{
                                    @"eventName": @"onLogin",
                                    @"credentials": credentials,
                                    @"profile": profile
                                    };
        mCallback(@[[NSNull null], loginData]);
        
    }
}

RCT_EXPORT_METHOD(login:(RCTResponseSenderBlock)callback) {
    mCallback = callback;
    [[GIDSignIn sharedInstance] signIn];
}

RCT_EXPORT_METHOD(logout) {
    [[GIDSignIn sharedInstance] signOut];
}

@end
