// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
import com.braintribe.console.ConsoleConfiguration;
import com.braintribe.console.ConsoleOutputs;
import com.braintribe.console.PrintStreamConsole;
import com.braintribe.console.output.ConfigurableConsoleOutputContainer;

public class ConsoleLab {
	public static void main(String[] args) {
		ConsoleConfiguration.install(new PrintStreamConsole(System.out, true));
		
		for (int i = 0; i < 10; i++) {
		
			ConfigurableConsoleOutputContainer configurableSequence = ConsoleOutputs.configurableSequence();
			configurableSequence.resetPosition(true);
			
			int partCount = 100;
			
			int percent = i * 100 / partCount;
			
			String progressMessage = String.format("Copied %d of %d artifact parts (%d%%) to target path  ", i, partCount, percent);
			configurableSequence.append(progressMessage);
			configurableSequence.append("\n");
			
			ConsoleOutputs.print(configurableSequence);
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
