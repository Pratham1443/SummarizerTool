from fastapi import FastAPI, Request
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_community.tools import DuckDuckGoSearchRun
from langchain.document_loaders import UnstructuredURLLoader
from langchain.docstore.document import Document
from unstructured.cleaners.core import remove_punctuation,clean,clean_extra_whitespace
from langchain.chains.summarize import load_summarize_chain
from dotenv import load_dotenv

load_dotenv()

app = FastAPI()

async def cleaned_website_content(url):
 """
 Returns a cleaned version of the website content.
 :param url: website url
 :return: Langchain document which can be used for summarization.
 """
 doc_loader = UnstructuredURLLoader(urls=[url],
 mode="elements",
 post_processors=[clean,remove_punctuation,clean_extra_whitespace])
 all_elements = await doc_loader.aload()
 descriptive_elements = [e for e in all_elements if e.metadata['category']=="NarrativeText"]
 cleaned_content = " ".join([e.page_content for e in descriptive_elements])
 return Document(page_content=cleaned_content, metadata={"source":url})

async def summarize_website(url, llm):
    chain = load_summarize_chain(llm, chain_type="stuff")
    clean_content = await cleaned_website_content(url)
    result = await chain.ainvoke([clean_content])
    return result['output_text']

@app.post("/summarize")
async def summarize(request: Request):
    # Call OpenAI API to summarize the URL content
    data = await request.json()
    url = data.get("url")
    llm = ChatOpenAI(model="gpt-3.5-turbo", temperature=0)
    summary = await summarize_website(url, llm)
    return summary
