import { PlusCircleOutlined } from "@ant-design/icons";
import { Button, Table } from "antd";
import axios from "axios";
import { useEffect, useState } from "react";
import CreateUserModal from "../components/modal/create-user.modal";

interface IUser {
  id: number;
  name: string;
  email: string;
}
const UserPage = () => {
  const [users, setUsers] = useState<IUser[]>([]);
  const [openCreateModal, setOpenCreateModal] = useState<boolean>(false);
  const getAllUsers = async () => {
    const response = await axios.get("http://localhost:8080/users");
    console.log(">>> check response: ", response);
    if (response?.data?.status === "Success") {
      setUsers(response.data.data);
    }
  };
  useEffect(() => {
    getAllUsers();
  }, []);

  const columns = [
    {
      title: "Id",
      dataIndex: "id",
    },
    {
      title: "Name",
      dataIndex: "name",
    },
    {
      title: "Email",
      dataIndex: "email",
    },
  ];

  return (
    <div style={{ padding: 10 }}>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <h3>Table Users</h3>
        <Button
          type="primary"
          icon={<PlusCircleOutlined />}
          onClick={() => setOpenCreateModal(true)}
        >
          Add a User
        </Button>
      </div>
      <Table bordered dataSource={users} columns={columns} rowKey={"id"} />
      <CreateUserModal
        openCreateModal={openCreateModal}
        setOpenCreateModal={setOpenCreateModal}
      />
    </div>
  );
};

export default UserPage;
