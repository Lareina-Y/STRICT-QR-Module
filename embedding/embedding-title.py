from sentence_transformers import SentenceTransformer
import os

# repoNames = ["ecf", "eclipse.jdt.core", "eclipse.jdt.debug", "eclipse.jdt.ui", "eclipse.pde.ui", "tomcat70"]
repoNames = ["adempiere-3.1.0", "apache-nutch-1.8", "apache-nutch-2.1", "atunes-1.10.0", "bookkeeper-4.1.0", 
             "commons-math-3-3.0", "derby-10.9.1.0", "eclipse-2.0", "jedit-4.2", "lang", "mahout-0.4", 
             "mahout-0.8", "math", "openjpa-2.0.1", "tika-1.3", "time"]

packageName = "STRICT-Replication-Package"

model = SentenceTransformer('sentence-transformers/all-mpnet-base-v2')

for repoName in repoNames:
    folder_path = f"{packageName}/ChangeReqs-Title/{repoName}"
    output_folder_path = f"{packageName}/ChangeReqs-Title-Emb/{repoName}"
    for filename in os.listdir(folder_path):
        if filename.endswith('.txt'):  # Assuming all files are text files
            file_path = os.path.join(folder_path, filename)
            with open(file_path, 'r') as file:
                first_line = file.readline()
                while not first_line:
                    first_line = file.readline()

            # Encode the title
            save_file_path = output_folder_path + f"/{filename}"
            with open(save_file_path, "w") as f:
                title = first_line.strip()  # Remove leading/trailing whitespaces
                embedding = model.encode(title)

                # Write title and its embedding to a file
                embedding_str = " ".join([str(value) for value in embedding])
                
                f.write(f"{title}\n{embedding_str}\n")

        print("Saved: " + repoName + "-" + filename)
