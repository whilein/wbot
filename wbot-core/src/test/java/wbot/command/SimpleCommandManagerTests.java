/*
 *    Copyright 2024 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package wbot.command;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author whilein
 */
public class SimpleCommandManagerTests {

    CommandManager commandManager;

    @BeforeEach
    public void setup() {
        commandManager = new SimpleCommandManager(mock());
    }

    @Test
    public void getCommandByName() {
        commandManager.register(new TestCommand("FOO", Collections.emptyList()));

        assertThat(commandManager.getCommand("foo"))
                .isNotNull();

        assertThat(commandManager.getCommand("Foo"))
                .isNotNull();

        assertThat(commandManager.getCommand("FOO"))
                .isNotNull();

        assertThat(commandManager.getCommand("bar"))
                .isNull();
    }

    @Test
    public void getCommandByAlias() {
        commandManager.register(new TestCommand("foo", Arrays.asList("bar", "baz")));

        assertThat(commandManager.getCommand("bar"))
                .isNotNull()
                .extracting(Command::name)
                .isEqualTo("foo");

        assertThat(commandManager.getCommand("baz"))
                .isNotNull()
                .extracting(Command::name)
                .isEqualTo("foo");

        assertThat(commandManager.getCommand("quux"))
                .isNull();
    }

    @Test
    public void unregisterByName() {
        val command = new TestCommand("foo", List.of());

        commandManager.register(command);

        assertThat(commandManager.getCommand("foo"))
                .isNotNull();

        commandManager.unregister("foo");

        assertThat(commandManager.getCommand("foo"))
                .isNull();
    }


    @Test
    public void unregisterByAlias() {
        val command = new TestCommand("foo", Arrays.asList("bar"));

        commandManager.register(command);

        assertThat(commandManager.getCommand("foo"))
                .isNotNull();

        commandManager.unregister("bar");

        assertThat(commandManager.getCommand("foo"))
                .isNull();
    }

    @Test
    public void unregister() {
        val command = new TestCommand("foo", Collections.emptyList());

        commandManager.register(command);

        assertThat(commandManager.getCommand("foo"))
                .isNotNull();

        commandManager.unregister(command);

        assertThat(commandManager.getCommand("foo"))
                .isNull();
    }

    private static final class TestCommand extends Command {

        public TestCommand(String name, List<String> aliases) {
            super(name, aliases);
        }

        @Override
        public void execute(CommandContext context) {
            // no-op
        }
    }

}
