/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.nthuchat;

public class FriendlyMessage {

    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String uid;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl, String uid) {
        this.text = text;
        this.name = name;
        if (photoUrl !=null && photoUrl.contains("..")) {
            this.photoUrl = "https://nthuchat.com" + photoUrl.replace("..", "");
        }else{
            this.photoUrl = photoUrl;
        }
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        if (text.contains("\n")){
            String tmp_text = text.replaceAll("\n"," ");
            this.text = tmp_text.trim();
        }else {
            this.text = text.trim();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        if (photoUrl !=null && photoUrl.contains("..")) {
            photoUrl = "https://nthuchat.com" + photoUrl.replace("..", "");
            return photoUrl;
        }else{
            return photoUrl;
        }
    }

    public String getText() {
        if (text.contains("\n")){
            String tmp_text = text.replaceAll("\n"," ");
            return tmp_text.trim();
        }else {
            return text.trim();
        }
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
