/*     */ package java.net;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import sun.net.util.IPAddressUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class URLStreamHandler
/*     */ {
/*     */   protected abstract URLConnection openConnection(URL paramURL) throws IOException;
/*     */   
/*     */   protected URLConnection openConnection(URL paramURL, Proxy paramProxy) throws IOException {
/*  96 */     throw new UnsupportedOperationException("Method not implemented.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2) {
/* 126 */     String str1 = paramURL.getProtocol();
/* 127 */     String str2 = paramURL.getAuthority();
/* 128 */     String str3 = paramURL.getUserInfo();
/* 129 */     String str4 = paramURL.getHost();
/* 130 */     int i = paramURL.getPort();
/* 131 */     String str5 = paramURL.getPath();
/* 132 */     String str6 = paramURL.getQuery();
/*     */ 
/*     */     
/* 135 */     String str7 = paramURL.getRef();
/*     */     
/* 137 */     boolean bool1 = false;
/* 138 */     boolean bool2 = false;
/*     */ 
/*     */ 
/*     */     
/* 142 */     if (paramInt1 < paramInt2) {
/* 143 */       int k = paramString.indexOf('?');
/* 144 */       bool2 = (k == paramInt1) ? true : false;
/* 145 */       if (k != -1 && k < paramInt2) {
/* 146 */         str6 = paramString.substring(k + 1, paramInt2);
/* 147 */         if (paramInt2 > k)
/* 148 */           paramInt2 = k; 
/* 149 */         paramString = paramString.substring(0, k);
/*     */       } 
/*     */     } 
/*     */     
/* 153 */     int j = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 159 */     boolean bool3 = (paramInt1 <= paramInt2 - 4 && paramString.charAt(paramInt1) == '/' && paramString.charAt(paramInt1 + 1) == '/' && paramString.charAt(paramInt1 + 2) == '/' && paramString.charAt(paramInt1 + 3) == '/') ? true : false;
/* 160 */     if (!bool3 && paramInt1 <= paramInt2 - 2 && paramString.charAt(paramInt1) == '/' && paramString
/* 161 */       .charAt(paramInt1 + 1) == '/') {
/* 162 */       paramInt1 += 2;
/* 163 */       j = paramString.indexOf('/', paramInt1);
/* 164 */       if (j < 0) {
/* 165 */         j = paramString.indexOf('?', paramInt1);
/* 166 */         if (j < 0) {
/* 167 */           j = paramInt2;
/*     */         }
/*     */       } 
/* 170 */       str4 = str2 = paramString.substring(paramInt1, j);
/*     */       
/* 172 */       int k = str2.indexOf('@');
/* 173 */       if (k != -1) {
/* 174 */         str3 = str2.substring(0, k);
/* 175 */         str4 = str2.substring(k + 1);
/*     */       } else {
/* 177 */         str3 = null;
/*     */       } 
/* 179 */       if (str4 != null) {
/*     */ 
/*     */         
/* 182 */         if (str4.length() > 0 && str4.charAt(0) == '[') {
/* 183 */           if ((k = str4.indexOf(']')) > 2) {
/*     */             
/* 185 */             String str = str4;
/* 186 */             str4 = str.substring(0, k + 1);
/*     */             
/* 188 */             if (!IPAddressUtil.isIPv6LiteralAddress(str4.substring(1, k))) {
/* 189 */               throw new IllegalArgumentException("Invalid host: " + str4);
/*     */             }
/*     */ 
/*     */             
/* 193 */             i = -1;
/* 194 */             if (str.length() > k + 1) {
/* 195 */               if (str.charAt(k + 1) == ':') {
/* 196 */                 k++;
/*     */                 
/* 198 */                 if (str.length() > k + 1) {
/* 199 */                   i = Integer.parseInt(str.substring(k + 1));
/*     */                 }
/*     */               } else {
/* 202 */                 throw new IllegalArgumentException("Invalid authority field: " + str2);
/*     */               } 
/*     */             }
/*     */           } else {
/*     */             
/* 207 */             throw new IllegalArgumentException("Invalid authority field: " + str2);
/*     */           } 
/*     */         } else {
/*     */           
/* 211 */           k = str4.indexOf(':');
/* 212 */           i = -1;
/* 213 */           if (k >= 0) {
/*     */             
/* 215 */             if (str4.length() > k + 1) {
/* 216 */               i = Integer.parseInt(str4.substring(k + 1));
/*     */             }
/* 218 */             str4 = str4.substring(0, k);
/*     */           } 
/*     */         } 
/*     */       } else {
/* 222 */         str4 = "";
/*     */       } 
/* 224 */       if (i < -1) {
/* 225 */         throw new IllegalArgumentException("Invalid port number :" + i);
/*     */       }
/* 227 */       paramInt1 = j;
/*     */ 
/*     */       
/* 230 */       if (str2 != null && str2.length() > 0) {
/* 231 */         str5 = "";
/*     */       }
/*     */     } 
/* 234 */     if (str4 == null) {
/* 235 */       str4 = "";
/*     */     }
/*     */ 
/*     */     
/* 239 */     if (paramInt1 < paramInt2) {
/* 240 */       if (paramString.charAt(paramInt1) == '/') {
/* 241 */         str5 = paramString.substring(paramInt1, paramInt2);
/* 242 */       } else if (str5 != null && str5.length() > 0) {
/* 243 */         bool1 = true;
/* 244 */         int k = str5.lastIndexOf('/');
/* 245 */         String str = "";
/* 246 */         if (k == -1 && str2 != null) {
/* 247 */           str = "/";
/*     */         }
/* 249 */         str5 = str5.substring(0, k + 1) + str + paramString.substring(paramInt1, paramInt2);
/*     */       } else {
/*     */         
/* 252 */         String str = (str2 != null) ? "/" : "";
/* 253 */         str5 = str + paramString.substring(paramInt1, paramInt2);
/*     */       } 
/* 255 */     } else if (bool2 && str5 != null) {
/* 256 */       int k = str5.lastIndexOf('/');
/* 257 */       if (k < 0)
/* 258 */         k = 0; 
/* 259 */       str5 = str5.substring(0, k) + "/";
/*     */     } 
/* 261 */     if (str5 == null) {
/* 262 */       str5 = "";
/*     */     }
/* 264 */     if (bool1) {
/*     */       
/* 266 */       while ((j = str5.indexOf("/./")) >= 0) {
/* 267 */         str5 = str5.substring(0, j) + str5.substring(j + 2);
/*     */       }
/*     */       
/* 270 */       j = 0;
/* 271 */       while ((j = str5.indexOf("/../", j)) >= 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 278 */         if (j > 0 && (paramInt2 = str5.lastIndexOf('/', j - 1)) >= 0 && str5
/* 279 */           .indexOf("/../", paramInt2) != 0) {
/* 280 */           str5 = str5.substring(0, paramInt2) + str5.substring(j + 3);
/* 281 */           j = 0; continue;
/*     */         } 
/* 283 */         j += 3;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 288 */       j = str5.indexOf("/..");
/* 289 */       while (str5.endsWith("/..") && (paramInt2 = str5.lastIndexOf('/', j - 1)) >= 0) {
/* 290 */         str5 = str5.substring(0, paramInt2 + 1);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 296 */       if (str5.startsWith("./") && str5.length() > 2) {
/* 297 */         str5 = str5.substring(2);
/*     */       }
/*     */       
/* 300 */       if (str5.endsWith("/.")) {
/* 301 */         str5 = str5.substring(0, str5.length() - 1);
/*     */       }
/*     */     } 
/* 304 */     setURL(paramURL, str1, str4, i, str2, str3, str5, str6, str7);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getDefaultPort() {
/* 314 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean equals(URL paramURL1, URL paramURL2) {
/* 330 */     String str1 = paramURL1.getRef();
/* 331 */     String str2 = paramURL2.getRef();
/* 332 */     return ((str1 == str2 || (str1 != null && str1.equals(str2))) && 
/* 333 */       sameFile(paramURL1, paramURL2));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int hashCode(URL paramURL) {
/* 345 */     int i = 0;
/*     */ 
/*     */     
/* 348 */     String str1 = paramURL.getProtocol();
/* 349 */     if (str1 != null) {
/* 350 */       i += str1.hashCode();
/*     */     }
/*     */     
/* 353 */     InetAddress inetAddress = getHostAddress(paramURL);
/* 354 */     if (inetAddress != null) {
/* 355 */       i += inetAddress.hashCode();
/*     */     } else {
/* 357 */       String str = paramURL.getHost();
/* 358 */       if (str != null) {
/* 359 */         i += str.toLowerCase().hashCode();
/*     */       }
/*     */     } 
/*     */     
/* 363 */     String str2 = paramURL.getFile();
/* 364 */     if (str2 != null) {
/* 365 */       i += str2.hashCode();
/*     */     }
/*     */     
/* 368 */     if (paramURL.getPort() == -1) {
/* 369 */       i += getDefaultPort();
/*     */     } else {
/* 371 */       i += paramURL.getPort();
/*     */     } 
/*     */     
/* 374 */     String str3 = paramURL.getRef();
/* 375 */     if (str3 != null) {
/* 376 */       i += str3.hashCode();
/*     */     }
/* 378 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean sameFile(URL paramURL1, URL paramURL2) {
/* 394 */     if (paramURL1.getProtocol() != paramURL2.getProtocol() && (paramURL1
/* 395 */       .getProtocol() == null || 
/* 396 */       !paramURL1.getProtocol().equalsIgnoreCase(paramURL2.getProtocol()))) {
/* 397 */       return false;
/*     */     }
/*     */     
/* 400 */     if (paramURL1.getFile() != paramURL2.getFile() && (paramURL1
/* 401 */       .getFile() == null || !paramURL1.getFile().equals(paramURL2.getFile()))) {
/* 402 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 406 */     int i = (paramURL1.getPort() != -1) ? paramURL1.getPort() : paramURL1.handler.getDefaultPort();
/* 407 */     int j = (paramURL2.getPort() != -1) ? paramURL2.getPort() : paramURL2.handler.getDefaultPort();
/* 408 */     if (i != j) {
/* 409 */       return false;
/*     */     }
/*     */     
/* 412 */     if (!hostsEqual(paramURL1, paramURL2)) {
/* 413 */       return false;
/*     */     }
/* 415 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized InetAddress getHostAddress(URL paramURL) {
/* 428 */     if (paramURL.hostAddress != null) {
/* 429 */       return paramURL.hostAddress;
/*     */     }
/* 431 */     String str = paramURL.getHost();
/* 432 */     if (str == null || str.equals("")) {
/* 433 */       return null;
/*     */     }
/*     */     try {
/* 436 */       paramURL.hostAddress = InetAddress.getByName(str);
/* 437 */     } catch (UnknownHostException unknownHostException) {
/* 438 */       return null;
/* 439 */     } catch (SecurityException securityException) {
/* 440 */       return null;
/*     */     } 
/*     */     
/* 443 */     return paramURL.hostAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean hostsEqual(URL paramURL1, URL paramURL2) {
/* 455 */     InetAddress inetAddress1 = getHostAddress(paramURL1);
/* 456 */     InetAddress inetAddress2 = getHostAddress(paramURL2);
/*     */     
/* 458 */     if (inetAddress1 != null && inetAddress2 != null) {
/* 459 */       return inetAddress1.equals(inetAddress2);
/*     */     }
/* 461 */     if (paramURL1.getHost() != null && paramURL2.getHost() != null) {
/* 462 */       return paramURL1.getHost().equalsIgnoreCase(paramURL2.getHost());
/*     */     }
/* 464 */     return (paramURL1.getHost() == null && paramURL2.getHost() == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String toExternalForm(URL paramURL) {
/* 477 */     int i = paramURL.getProtocol().length() + 1;
/* 478 */     if (paramURL.getAuthority() != null && paramURL.getAuthority().length() > 0)
/* 479 */       i += 2 + paramURL.getAuthority().length(); 
/* 480 */     if (paramURL.getPath() != null) {
/* 481 */       i += paramURL.getPath().length();
/*     */     }
/* 483 */     if (paramURL.getQuery() != null) {
/* 484 */       i += 1 + paramURL.getQuery().length();
/*     */     }
/* 486 */     if (paramURL.getRef() != null) {
/* 487 */       i += 1 + paramURL.getRef().length();
/*     */     }
/* 489 */     StringBuffer stringBuffer = new StringBuffer(i);
/* 490 */     stringBuffer.append(paramURL.getProtocol());
/* 491 */     stringBuffer.append(":");
/* 492 */     if (paramURL.getAuthority() != null && paramURL.getAuthority().length() > 0) {
/* 493 */       stringBuffer.append("//");
/* 494 */       stringBuffer.append(paramURL.getAuthority());
/*     */     } 
/* 496 */     if (paramURL.getPath() != null) {
/* 497 */       stringBuffer.append(paramURL.getPath());
/*     */     }
/* 499 */     if (paramURL.getQuery() != null) {
/* 500 */       stringBuffer.append('?');
/* 501 */       stringBuffer.append(paramURL.getQuery());
/*     */     } 
/* 503 */     if (paramURL.getRef() != null) {
/* 504 */       stringBuffer.append("#");
/* 505 */       stringBuffer.append(paramURL.getRef());
/*     */     } 
/* 507 */     return stringBuffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setURL(URL paramURL, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7) {
/* 532 */     if (this != paramURL.handler) {
/* 533 */       throw new SecurityException("handler for url different from this handler");
/*     */     }
/*     */ 
/*     */     
/* 537 */     paramURL.set(paramURL.getProtocol(), paramString2, paramInt, paramString3, paramString4, paramString5, paramString6, paramString7);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void setURL(URL paramURL, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4) {
/* 563 */     String str1 = null;
/* 564 */     String str2 = null;
/* 565 */     if (paramString2 != null && paramString2.length() != 0) {
/* 566 */       str1 = (paramInt == -1) ? paramString2 : (paramString2 + ":" + paramInt);
/* 567 */       int i = paramString2.lastIndexOf('@');
/* 568 */       if (i != -1) {
/* 569 */         str2 = paramString2.substring(0, i);
/* 570 */         paramString2 = paramString2.substring(i + 1);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 577 */     String str3 = null;
/* 578 */     String str4 = null;
/* 579 */     if (paramString3 != null) {
/* 580 */       int i = paramString3.lastIndexOf('?');
/* 581 */       if (i != -1) {
/* 582 */         str4 = paramString3.substring(i + 1);
/* 583 */         str3 = paramString3.substring(0, i);
/*     */       } else {
/* 585 */         str3 = paramString3;
/*     */       } 
/* 587 */     }  setURL(paramURL, paramString1, paramString2, paramInt, str1, str2, str3, str4, paramString4);
/*     */   }
/*     */ }


/* Location:              D:\毕设研究\java\rt.jar!\java\net\URLStreamHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */