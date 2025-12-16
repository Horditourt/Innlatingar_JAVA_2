package alienmarauders.menu.chatmenu;

/**
 * Represents one line in the chat UI.
 * <p>
 * This is a UI-level model (not part of the network protocol).
 */
public final class ChatLine {

    private final ChatLineKind kind;
    private final String from;
    private final String text;
    private final boolean self;

    private ChatLine(ChatLineKind kind, String from, String text, boolean self) {
        this.kind = kind;
        this.from = from;
        this.text = text;
        this.self = self;
    }

    /**
     * Creates a chat message line.
     *
     * @param from   username
     * @param text   message text
     * @param isSelf whether the local user sent it
     * @return a chat line instance
     */
    public static ChatLine chat(String from, String text, boolean isSelf) {
        return new ChatLine(ChatLineKind.CHAT, from, text, isSelf);
    }

    /**
     * Creates a system message line.
     *
     * @param text system text
     * @return a system line instance
     */
    public static ChatLine system(String text) {
        return new ChatLine(ChatLineKind.SYSTEM, null, text, false);
    }

    /**
     * Returns the kind of this line.
     *
     * @return the line kind
     */
    public ChatLineKind getKind() {
        return kind;
    }

    /**
     * Returns the sender username.
     *
     * @return username or {@code null} for system lines
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the text content.
     *
     * @return message/system text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns whether this line was sent by the local user.
     *
     * @return {@code true} if local user's message
     */
    public boolean isSelf() {
        return self;
    }
}
