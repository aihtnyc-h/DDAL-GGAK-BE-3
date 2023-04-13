// package com.ddalggak.finalproject.global.nginx;
//
// import org.springframework.core.env.Environment;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequiredArgsConstructor
// public class WebRestController {
//
// 	private final Environment env;
//
// 	@GetMapping("/profile")
// 	public String getProfile() {
// 		String[] str = env.getActiveProfiles();
// 		int idx = 0;
// 		for(int i = 0; i < str.length; i++){
// 			if(str[i].contains("set")){
// 				idx = i;
// 				break;
// 			}
// 		}
//
// 		return str[idx];
// 	}
// }
//
// // @RestController
// // @RequiredArgsConstructor
// // public class WebRestController {
// //
// // 	private final Environment env;
// //
// // 	@GetMapping("/profile")
// // 	public String getProfile() {
// // 		String[] str = env.getActiveProfiles();
// // 		if (str.length == 0) {
// // 			return "No active profiles found";
// // 		}
// //
// // 		int idx = -1;
// // 		for(int i = 0; i < str.length; i++){
// // 			if(str[i].contains("set")){
// // 				idx = i;
// // 				break;
// // 			}
// // 		}
// //
// // 		if (idx >= 0) {
// // 			return str[idx];
// // 		} else {
// // 			return "No active profile containing 'set' found";
// // 		}
// // 	}
// // }
