import requests
import json
import uuid

MCP_URL = "http://localhost:3333/mcp"
HEADERS = {
    "Content-Type": "application/json"
}

def mcp_request(method, params=None):
    payload = {
        "jsonrpc": "2.0",
        "id": str(uuid.uuid4()),
        "method": method,
        "params": params or {}
    }

    response = requests.post(
        MCP_URL,
        headers=HEADERS,
        data=json.dumps(payload),
        timeout=10
    )

    response.raise_for_status()
    return response.json()


def test_health():
    print("🔍 Testando conexão com MCP...")
    try:
        response = mcp_request("ping")
        print("✅ MCP respondeu:", response)
    except Exception as e:
        print("❌ Falha ao conectar no MCP:", e)


def test_list_tools():
    print("\n🔧 Listando tools do MCP...")
    try:
        response = mcp_request("tools/list")
        tools = response.get("result", [])
        print(f"✅ {len(tools)} tools encontradas:")
        for tool in tools:
            print(f" - {tool.get('name')}: {tool.get('description')}")
        return tools
    except Exception as e:
        print("❌ Erro ao listar tools:", e)
        return []


def test_call_tool(tool_name, arguments):
    print(f"\n🚀 Chamando tool '{tool_name}'...")
    try:
        response = mcp_request(
            "tools/call",
            {
                "name": tool_name,
                "arguments": arguments
            }
        )
        print("✅ Resultado da tool:")
        print(json.dumps(response, indent=2, ensure_ascii=False))
    except Exception as e:
        print("❌ Erro ao chamar tool:", e)


if __name__ == "__main__":
    test_health()

    tools = test_list_tools()

    if tools:
        # Exemplo: chama a primeira tool com args vazios
        tool_name = tools[0]["name"]
        test_call_tool(tool_name, {})
