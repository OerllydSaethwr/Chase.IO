import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;

public class OpenAIClient {

  public static void main(String[] args) {
    OpenAiService service = new OpenAiService("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");
    CompletionRequest completionRequest = CompletionRequest.builder()
        .prompt("Somebody once told me the world is gonna roll me")
        .model("ada")
        .echo(true)
        .build();
    service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
  }
}
