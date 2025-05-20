package top.ourfor.app.iplay.page.web;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class ScriptManageViewModel  {
    @Getter @Setter
    String value;

    @Getter @Setter
    Consumer<String> onClick;

}
