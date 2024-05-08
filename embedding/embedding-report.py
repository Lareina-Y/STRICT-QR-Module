from sentence_transformers import SentenceTransformer
import os

# repoNames = ["ecf", "eclipse.jdt.core", "eclipse.jdt.debug", "eclipse.jdt.ui", "eclipse.pde.ui", "tomcat70"]
repoNames = ["ecf", "eclipse.jdt.core", "eclipse.jdt.debug", "eclipse.jdt.ui", "eclipse.pde.ui", "tomcat70",
             "adempiere-3.1.0", "apache-nutch-1.8", "apache-nutch-2.1", "atunes-1.10.0", "bookkeeper-4.1.0", 
             "commons-math-3-3.0", "derby-10.9.1.0", "eclipse-2.0", "jedit-4.2", "lang", "mahout-0.4", 
             "mahout-0.8", "math", "openjpa-2.0.1", "tika-1.3", "time"]

packageName = "STRICT-Replication-Package"

model = SentenceTransformer('sentence-transformers/all-mpnet-base-v2')

for repoName in repoNames:
    folder_path = f"{packageName}/ChangeReqs-Dec-Normalized/{repoName}"
    output_folder_path = f"{packageName}/ChangeReqs-Dec-Normalized-Emb/{repoName}"
    for filename in os.listdir(folder_path):
        if filename.endswith('.txt'):  # Assuming all files are text files
            file_path = os.path.join(folder_path, filename)
            with open(file_path, 'r') as file:
                lines = file.readlines()

            # Tokenize and encode each token in each sentence
            save_file_path = output_folder_path + f"/{filename}"
            with open(save_file_path, "w") as f:
                for line in lines:
                    if line != '\n':  # Skip empty lines
                        sentence = line.strip()  # Remove leading/trailing whitespaces
                        tokens = sentence.split()  # Split by whitespace
                        embeddings = model.encode(tokens)

                        # Write word and its embedding to a file
                        for token, embedding in zip(tokens, embeddings):
                            embedding_str = " ".join([str(value) for value in embedding])
                            f.write(f"{token}\t{embedding_str}\n")
                        # Add an empty line after each sentence for separation
                        f.write('\n')

        print("Saved: " + repoName + "-" + filename)
