package com.example.acme.assist.model;

import com.azure.ai.openai.models.ChatRole;

import java.util.List;

public class AcmeChatRequest {

    /**
     * (Optional) Name of the request page. Used as the context for the
     * conversation.
     */
    private String page;

    /**
     * (Optional) ID of the product. Used as the context for the conversation.
     */
    private String productId;

    /**
     * The chat history of the conversation. The last message must be in the user
     * role.
     */
    private List<Message> messages;

    /**
     * @return the page
     */
    public String getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(String page) {
        this.page = page;
    }

    /**
     * @return the productId
     */
    public String getProductId() {
        return productId;
    }

    /**
     * @param productId the productId to set
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * @return the messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public static class Message {

        private ChatRole role;

        private String content;

        /**
         * @return the role
         */
        public ChatRole getRole() {
            return role;
        }

        /**
         * @param role the role to set
         */
        public void setRole(ChatRole role) {
            this.role = role;
        }

        /**
         * @return the content
         */
        public String getContent() {
            return content;
        }

        /**
         * @param content the content to set
         */
        public void setContent(String content) {
            this.content = content;
        }
    }
}
