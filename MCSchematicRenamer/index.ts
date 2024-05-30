import * as NBT from "nbtify";
import * as fs from "node:fs/promises";

interface StructureFile {
  structure: {
    entities: Array<{
      Item: {
        tag: {
          display: {
            Name: string;
          };
        };
      };
    }>;
  };
}

function getItemName(data: NBT.NBTData): string {
  return (<StructureFile>(<unknown>data.data)).structure.entities[0].Item.tag
    .display.Name;
}

function setItemName(data: NBT.NBTData, name: string): NBT.NBTData {
  (<StructureFile>(
    (<unknown>data.data)
  )).structure.entities[0].Item.tag.display.Name = name;
  return data;
}

async function createNewItem(
  data: NBT.NBTData,
  filepath: string,
  name: string
) {
  data = setItemName(data, name);
  const intArray: Uint8Array = await NBT.write(data);
  await fs.writeFile(
    filepath + "/structures/" + name + ".mcstructure",
    intArray
  );
}

async function main() {
  if (process.argv.length !== 5) {
    console.error(
      "Usage: bun index.ts <structure file> <name list file> <output fileName>"
    );
    process.exit(1);
  }

  const structureFileName: string = process.argv[2];
  const nameListFileName: string = process.argv[3];
  const outputFileName: string = process.argv[4];

  const nameList: string[] = (
    await fs.readFile(nameListFileName, "utf-8")
  ).split("\n");

  const buffer: Buffer = await fs.readFile(structureFileName);
  const data: NBT.NBTData = await NBT.read(buffer);

  await fs.mkdir(outputFileName + "/structures", { recursive: true });
  nameList.forEach(async (name) => {
    await createNewItem(data, outputFileName, name);
  });

  // zip the output folder
}

main();
